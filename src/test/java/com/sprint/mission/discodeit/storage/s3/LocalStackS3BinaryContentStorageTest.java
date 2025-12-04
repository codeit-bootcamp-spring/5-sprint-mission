package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.common.config.properties.S3Properties;
import com.sprint.mission.discodeit.domain.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.infra.storage.s3.S3BinaryContentStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.Duration;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.StorageTestFixtures.NEW_CONTENT;
import static com.sprint.mission.discodeit.support.StorageTestFixtures.ORIGINAL_CONTENT;
import static com.sprint.mission.discodeit.support.StorageTestFixtures.TEST_CONTENT_ENGLISH;
import static com.sprint.mission.discodeit.support.StorageTestFixtures.createBinaryContentDto;
import static com.sprint.mission.discodeit.support.StorageTestFixtures.createLargeContent;
import static com.sprint.mission.discodeit.support.StorageTestFixtures.createTestContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
@DisplayName("LocalStack 기반 S3BinaryContentStorage 테스트")
class LocalStackS3BinaryContentStorageTest {

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
        ListObjectsV2Response listResponse = s3Client.listObjectsV2(ListObjectsV2Request.builder()
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
        HeadObjectResponse headResponse = s3Client.headObject(HeadObjectRequest.builder()
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
        ResponseBytes<GetObjectResponse> getResponse = s3Client.getObjectAsBytes(GetObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(id.toString())
            .build());

        assertThat(getResponse.asByteArray()).isEqualTo(newContent);
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
    void putMultipleFiles() {
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
        assertThat(getS3Object(id1)).isEqualTo(content1);
        assertThat(getS3Object(id2)).isEqualTo(content2);
        assertThat(getS3Object(id3)).isEqualTo(content3);
    }

    @Test
    @DisplayName("큰 파일 저장 및 조회")
    void putLargeFile() {
        // given
        UUID id = UUID.randomUUID();
        byte[] largeContent = createLargeContent();

        // when
        UUID result = storage.put(id, largeContent);

        // then
        assertThat(result).isEqualTo(id);
        assertThat(getS3Object(id)).isEqualTo(largeContent);
    }

    private byte[] getS3Object(UUID id) {
        return s3Client.getObjectAsBytes(GetObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(id.toString())
            .build()).asByteArray();
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
}
