package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.properties.S3Properties;
import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@DisplayName("S3BinaryContentStorage 테스트")
final class S3BinaryContentStorageTest {

    private S3BinaryContentStorageTest() {
    }

    private static final String TEST_CONTENT = "테스트 데이터";
    private static final String TEST_CONTENT_ENGLISH = "test content";
    private static final String ORIGINAL_CONTENT = "original content";
    private static final String NEW_CONTENT = "new content";
    private static final int LARGE_FILE_SIZE_MB = 1;
    private static final int BYTES_PER_MB = 1024 * 1024;

    static byte[] createTestContent(String content) {
        return content.getBytes();
    }

    static byte[] createLargeContent() {
        byte[] content = new byte[LARGE_FILE_SIZE_MB * BYTES_PER_MB];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }
        return content;
    }

    static BinaryContentDto createBinaryContentDto(UUID id, String fileName, long size, String contentType) {
        return new BinaryContentDto(id, fileName, size, contentType);
    }

    static void assertStreamContentEquals(InputStream stream, byte[] expected) throws IOException {
        try (stream) {
            byte[] actual = stream.readAllBytes();
            assertThat(actual).isEqualTo(expected);
        }
    }

    @SpringBootTest
    @Nested
    @DisplayName("실제 AWS S3 통합 테스트")
    @Disabled
    class RealS3IntegrationTest {

        private final UUID testId = UUID.randomUUID();
        private final byte[] testData = createTestContent(TEST_CONTENT);

        @Autowired
        private S3BinaryContentStorage s3BinaryContentStorage;

        @Value("${discodeit.storage.s3.bucket}")
        private String bucket;

        @Autowired
        private S3Client s3Client;

        @AfterEach
        void tearDown() {
            try {
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(testId.toString())
                    .build();

                s3Client.deleteObject(deleteRequest);
            } catch (NoSuchKeyException e) {
                // 객체가 이미 없는 경우는 무시
            }
        }

        @Test
        @DisplayName("S3에 파일 업로드 성공")
        void putSuccess() {
            // when
            UUID resultId = s3BinaryContentStorage.put(testId, testData);

            // then
            assertThat(resultId).isEqualTo(testId);
        }

        @Test
        @DisplayName("S3에서 파일 다운로드 성공")
        void getSuccess() throws IOException {
            // given
            s3BinaryContentStorage.put(testId, testData);

            // when
            InputStream result = s3BinaryContentStorage.get(testId);

            // then
            assertThat(result).isNotNull();
            assertStreamContentEquals(result, testData);
        }

        @Test
        @DisplayName("존재하지 않는 파일 조회 시 예외 발생")
        void getNotFound() {
            // when & then
            assertThatThrownBy(() -> s3BinaryContentStorage.get(UUID.randomUUID()))
                .isInstanceOf(BinaryContentNotFoundException.class);
        }

        @Test
        @DisplayName("Presigned URL 생성 성공")
        void downloadSuccess() {
            // given
            s3BinaryContentStorage.put(testId, testData);
            BinaryContentDto dto = createBinaryContentDto(
                testId, "test.txt", testData.length, "text/plain"
            );

            // when
            ResponseEntity<Void> response = s3BinaryContentStorage.download(dto);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
            assertThat(response.getHeaders().getLocation()).isNotNull();

            String location = response.getHeaders().getLocation().toString();
            assertThat(location).contains(bucket);
            assertThat(location).contains(testId.toString());
        }
    }

    @Testcontainers
    @Nested
    @DisplayName("LocalStack 기반 단위 테스트")
    class LocalStackUnitTest {

        private static final String BUCKET_NAME = "test-bucket";

        @Container
        static LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.5.0"))
            .withServices(S3);

        private S3BinaryContentStorage storage;
        private S3Client s3Client;

        @BeforeEach
        void setUp() {
            // S3 클라이언트 생성
            s3Client = S3Client.builder()
                .endpointOverride(localStack.getEndpointOverride(S3))
                .region(Region.of(localStack.getRegion()))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                            localStack.getAccessKey(),
                            localStack.getSecretKey()
                        )
                    )
                )
                .build();

            // S3 Presigner 생성
            S3Presigner s3Presigner = S3Presigner.builder()
                .endpointOverride(localStack.getEndpointOverride(S3))
                .region(Region.of(localStack.getRegion()))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                            localStack.getAccessKey(),
                            localStack.getSecretKey()
                        )
                    )
                )
                .build();

            // 버킷 생성
            s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(BUCKET_NAME)
                .build());

            // S3Properties 생성
            S3Properties s3Properties = new S3Properties(
                localStack.getAccessKey(),
                localStack.getSecretKey(),
                localStack.getRegion(),
                BUCKET_NAME,
                Duration.ofMinutes(5)
            );

            // Storage 인스턴스 생성
            storage = new S3BinaryContentStorage(s3Client, s3Presigner, s3Properties);
        }

        @AfterEach
        void tearDown() {
            // 버킷 내 모든 객체 삭제
            var listResponse = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .build());

            listResponse.contents().forEach(s3Object -> s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(s3Object.key())
                .build()));

            s3Client.close();
        }

        @Test
        @DisplayName("파일 저장 성공")
        void putSuccess() {
            // given
            UUID id = UUID.randomUUID();
            byte[] content = createTestContent(TEST_CONTENT_ENGLISH);

            // when
            UUID result = storage.put(id, content);

            // then
            assertThat(result).isEqualTo(id);

            // S3에 실제로 저장되었는지 확인
            var headResponse = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(id.toString())
                .build());

            assertThat(headResponse.contentLength()).isEqualTo(content.length);
        }

        @Test
        @DisplayName("파일 저장 후 덮어쓰기")
        void putOverwrite() {
            // given
            UUID id = UUID.randomUUID();
            byte[] originalContent = createTestContent(ORIGINAL_CONTENT);
            byte[] newContent = createTestContent(NEW_CONTENT);

            storage.put(id, originalContent);

            // when
            storage.put(id, newContent);

            // then
            var getResponse = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(id.toString())
                .build());

            assertThat(getResponse.asByteArray()).isEqualTo(newContent);
        }

        @Test
        @DisplayName("파일 읽기 성공")
        void getSuccess() throws IOException {
            // given
            UUID id = UUID.randomUUID();
            byte[] content = createTestContent(TEST_CONTENT_ENGLISH);

            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(id.toString())
                    .build(),
                RequestBody.fromBytes(content)
            );

            // when
            InputStream inputStream = storage.get(id);

            // then
            assertThat(inputStream).isNotNull();
            assertStreamContentEquals(inputStream, content);
        }

        @Test
        @DisplayName("파일 읽기 실패 - 존재하지 않는 파일")
        void getFileNotFound() {
            // given
            UUID id = UUID.randomUUID();

            // when & then
            assertThatThrownBy(() -> storage.get(id))
                .isInstanceOf(BinaryContentNotFoundException.class);
        }

        @Test
        @DisplayName("파일 다운로드 - Presigned URL 생성 성공")
        void downloadSuccess() {
            // given
            UUID id = UUID.randomUUID();
            byte[] content = createTestContent(TEST_CONTENT_ENGLISH);

            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(id.toString())
                    .build(),
                RequestBody.fromBytes(content)
            );

            BinaryContentDto metaData = createBinaryContentDto(
                id,
                "test.txt",
                content.length,
                "text/plain"
            );

            // when
            ResponseEntity<Void> response = storage.download(metaData);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
            assertThat(response.getHeaders().getLocation()).isNotNull();
            assertThat(response.getHeaders().getLocation().toString())
                .contains(BUCKET_NAME)
                .contains(id.toString());
        }

        @Test
        @DisplayName("파일 다운로드 - 존재하지 않는 파일도 Presigned URL 생성")
        void downloadNonExistentFileGeneratesUrl() {
            // given
            UUID id = UUID.randomUUID();
            BinaryContentDto metaData = createBinaryContentDto(
                id,
                "notfound.txt",
                1024L,
                "text/plain"
            );

            // when
            ResponseEntity<Void> response = storage.download(metaData);

            // then
            // Presigned URL은 파일 존재 여부와 관계없이 생성됨
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
            assertThat(response.getHeaders().getLocation()).isNotNull();
        }

        @Test
        @DisplayName("여러 파일 저장 및 조회")
        void putMultipleFiles() throws IOException {
            // given
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            UUID id3 = UUID.randomUUID();

            byte[] content1 = createTestContent("content 1");
            byte[] content2 = createTestContent("content 2");
            byte[] content3 = createTestContent("content 3");

            // when
            storage.put(id1, content1);
            storage.put(id2, content2);
            storage.put(id3, content3);

            // then
            assertStreamContentEquals(storage.get(id1), content1);
            assertStreamContentEquals(storage.get(id2), content2);
            assertStreamContentEquals(storage.get(id3), content3);
        }

        @Test
        @DisplayName("큰 파일 저장 및 조회")
        void putLargeFile() throws IOException {
            // given
            UUID id = UUID.randomUUID();
            byte[] largeContent = createLargeContent();

            // when
            UUID result = storage.put(id, largeContent);

            // then
            assertThat(result).isEqualTo(id);
            assertStreamContentEquals(storage.get(id), largeContent);
        }

        @Test
        @DisplayName("ContentType이 포함된 파일 다운로드")
        void downloadWithContentType() {
            // given
            UUID id = UUID.randomUUID();
            byte[] content = new byte[]{0x12, 0x34, 0x56, 0x78};

            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(id.toString())
                    .contentType("image/jpeg")
                    .build(),
                RequestBody.fromBytes(content)
            );

            BinaryContentDto metaData = createBinaryContentDto(
                id,
                "image.jpg",
                content.length,
                "image/jpeg"
            );

            // when
            ResponseEntity<Void> response = storage.download(metaData);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
            assertThat(response.getHeaders().getLocation()).isNotNull();
            assertThat(response.getHeaders().getLocation().toString())
                .contains("response-content-type=image%2Fjpeg");
        }

        @Test
        @Disabled("LocalStack 버그: 빈 파일(0바이트) 업로드 시 'NoneType' object has no attribute 'to_bytes' 오류 발생. 실제 AWS S3에서는 정상 동작함")
        @DisplayName("빈 파일 저장 및 조회")
        void putEmptyFile() throws IOException {
            // given
            UUID id = UUID.randomUUID();
            byte[] emptyContent = new byte[0];

            // when
            UUID result = storage.put(id, emptyContent);

            // then
            assertThat(result).isEqualTo(id);
            assertStreamContentEquals(storage.get(id), emptyContent);
        }
    }
}
