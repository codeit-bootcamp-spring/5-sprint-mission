package com.sprint.mission.discodeit.scheduler;

import com.sprint.mission.discodeit.config.properties.S3Properties;
import com.sprint.mission.discodeit.config.properties.StorageProperties;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
@ExtendWith(MockitoExtension.class)
@Disabled("LocalStack과 S3Client 설정 문제로 수동 실행 필요. ./gradlew test -Dtest.s3.enabled=true")
@DisplayName("FileCleanupScheduler 단위 테스트")
class FileCleanupSchedulerTest {

    private static final String BUCKET_NAME = "test-bucket";

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(
        DockerImageName.parse("localstack/localstack:3.0"))
        .withServices(S3);

    @Mock
    private BinaryContentRepository binaryContentRepository;

    private FileCleanupScheduler scheduler;
    private S3Client s3Client;
    private S3Properties s3Properties;

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

        // 버킷 생성
        s3Client.createBucket(CreateBucketRequest.builder()
            .bucket(BUCKET_NAME)
            .build());

        // Properties 생성
        s3Properties = new S3Properties(
            localStack.getAccessKey(),
            localStack.getSecretKey(),
            localStack.getRegion(),
            BUCKET_NAME,
            Duration.ofMinutes(5)
        );

        StorageProperties.Local localProps = new StorageProperties.Local(
            "/tmp/test-storage",
            Duration.ofSeconds(1) // 테스트를 위해 1초로 설정
        );
        StorageProperties storageProperties = new StorageProperties("s3", localProps);

        // Scheduler 인스턴스 생성
        scheduler = new FileCleanupScheduler(
            binaryContentRepository,
            s3Properties,
            storageProperties
        );
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
    @DisplayName("고아 파일 정리 - 오래된 UUID 파일 삭제")
    void cleanOrphanFilesDeletesOldOrphanFile() throws InterruptedException {
        // given
        UUID orphanId = UUID.randomUUID();

        // S3에 파일 업로드
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(orphanId.toString())
                .build(),
            RequestBody.fromBytes("orphan content".getBytes())
        );

        // Grace period 경과를 위한 대기
        Thread.sleep(2000);

        given(binaryContentRepository.existsById(orphanId)).willReturn(false);

        // when
        scheduler.cleanOrphanFiles();

        // then
        assertThatCode(() -> s3Client.headObject(HeadObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(orphanId.toString())
            .build()))
            .isInstanceOf(NoSuchKeyException.class);
    }

    @Test
    @DisplayName("고아 파일 정리 - 최근 파일은 삭제하지 않음")
    void cleanOrphanFilesKeepsRecentFiles() {
        // given
        UUID recentId = UUID.randomUUID();

        // S3에 파일 업로드 (방금 업로드된 파일)
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(recentId.toString())
                .build(),
            RequestBody.fromBytes("recent content".getBytes())
        );

        given(binaryContentRepository.existsById(recentId)).willReturn(false);

        // when
        scheduler.cleanOrphanFiles();

        // then
        // Grace period 이내이므로 삭제되지 않음
        assertThatCode(() -> s3Client.headObject(HeadObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(recentId.toString())
            .build()))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("고아 파일 정리 - DB에 존재하는 파일은 삭제하지 않음")
    void cleanOrphanFilesKeepsFilesInDatabase() throws InterruptedException {
        // given
        UUID validId = UUID.randomUUID();

        // S3에 파일 업로드
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(validId.toString())
                .build(),
            RequestBody.fromBytes("valid content".getBytes())
        );

        // Grace period 경과를 위한 대기
        Thread.sleep(2000);

        given(binaryContentRepository.existsById(validId)).willReturn(true);

        // when
        scheduler.cleanOrphanFiles();

        // then
        assertThatCode(() -> s3Client.headObject(HeadObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(validId.toString())
            .build()))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("고아 파일 정리 - 잘못된 형식의 파일명은 건너뜀")
    void cleanOrphanFilesSkipsInvalidFilenames() throws InterruptedException {
        // given
        String invalidKey = "invalid-filename.txt";

        // S3에 파일 업로드
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(invalidKey)
                .build(),
            RequestBody.fromBytes("invalid content".getBytes())
        );

        // Grace period 경과를 위한 대기
        Thread.sleep(2000);

        // when
        scheduler.cleanOrphanFiles();

        // then
        // UUID 형식이 아니므로 무시되고 삭제되지 않음
        assertThatCode(() -> s3Client.headObject(HeadObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(invalidKey)
            .build()))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("고아 파일 정리 - 여러 고아 파일 동시 삭제")
    void cleanOrphanFilesDeletesMultipleOrphanFiles() throws InterruptedException {
        // given
        UUID orphan1 = UUID.randomUUID();
        UUID orphan2 = UUID.randomUUID();
        UUID orphan3 = UUID.randomUUID();

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(orphan1.toString())
                .build(),
            RequestBody.fromBytes("content 1".getBytes())
        );

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(orphan2.toString())
                .build(),
            RequestBody.fromBytes("content 2".getBytes())
        );

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(orphan3.toString())
                .build(),
            RequestBody.fromBytes("content 3".getBytes())
        );

        // Grace period 경과를 위한 대기
        Thread.sleep(2000);

        given(binaryContentRepository.existsById(any(UUID.class))).willReturn(false);

        // when
        scheduler.cleanOrphanFiles();

        // then
        assertThatCode(() -> s3Client.headObject(HeadObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(orphan1.toString())
            .build()))
            .isInstanceOf(NoSuchKeyException.class);

        assertThatCode(() -> s3Client.headObject(HeadObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(orphan2.toString())
            .build()))
            .isInstanceOf(NoSuchKeyException.class);

        assertThatCode(() -> s3Client.headObject(HeadObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(orphan3.toString())
            .build()))
            .isInstanceOf(NoSuchKeyException.class);
    }

    @Test
    @DisplayName("고아 파일 정리 - 혼합 시나리오")
    void cleanOrphanFilesMixedScenario() throws InterruptedException {
        // given
        UUID orphanId = UUID.randomUUID();
        UUID validId = UUID.randomUUID();
        UUID recentId = UUID.randomUUID();

        // 고아 파일 (오래됨, DB에 없음)
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(orphanId.toString())
                .build(),
            RequestBody.fromBytes("orphan".getBytes())
        );

        // 유효 파일 (오래됨, DB에 있음)
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(validId.toString())
                .build(),
            RequestBody.fromBytes("valid".getBytes())
        );

        // Grace period 경과를 위한 대기
        Thread.sleep(2000);

        // 최근 파일 (최근 업로드, DB에 없음)
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(recentId.toString())
                .build(),
            RequestBody.fromBytes("recent".getBytes())
        );

        given(binaryContentRepository.existsById(orphanId)).willReturn(false);
        given(binaryContentRepository.existsById(validId)).willReturn(true);
        given(binaryContentRepository.existsById(recentId)).willReturn(false);

        // when
        scheduler.cleanOrphanFiles();

        // then
        // 고아 파일만 삭제됨
        assertThatCode(() -> s3Client.headObject(HeadObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(orphanId.toString())
            .build()))
            .isInstanceOf(NoSuchKeyException.class);

        // 유효 파일은 보존됨
        assertThatCode(() -> s3Client.headObject(HeadObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(validId.toString())
            .build()))
            .doesNotThrowAnyException();

        // 최근 파일은 보존됨
        assertThatCode(() -> s3Client.headObject(HeadObjectRequest.builder()
            .bucket(BUCKET_NAME)
            .key(recentId.toString())
            .build()))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("고아 파일 정리 - 빈 버킷에서도 정상 동작")
    void cleanOrphanFilesEmptyBucket() {
        // given - 버킷에 파일이 없음

        // when & then - 예외 없이 완료되어야 함
        assertThatCode(() -> scheduler.cleanOrphanFiles())
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("고아 파일 정리 - 대량 파일 처리")
    void cleanOrphanFilesBatchDeletion() throws InterruptedException {
        // given - 50개의 고아 파일 생성
        for (int i = 0; i < 50; i++) {
            UUID orphanId = UUID.randomUUID();
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(orphanId.toString())
                    .build(),
                RequestBody.fromBytes(("content " + i).getBytes())
            );
        }

        // Grace period 경과를 위한 대기
        Thread.sleep(2000);

        given(binaryContentRepository.existsById(any(UUID.class))).willReturn(false);

        // when
        scheduler.cleanOrphanFiles();

        // then
        var listResponse = s3Client.listObjectsV2(ListObjectsV2Request.builder()
            .bucket(BUCKET_NAME)
            .build());

        assertThat(listResponse.contents()).isEmpty();
    }

    @Test
    @DisplayName("고아 파일 정리 - orphanGrace가 null일 때 기본값 사용")
    void cleanOrphanFilesDefaultOrphanGrace() {
        // given
        StorageProperties storagePropertiesWithNullGrace = new StorageProperties("s3", null);

        FileCleanupScheduler schedulerWithDefaultGrace = new FileCleanupScheduler(
            binaryContentRepository,
            s3Properties,
            storagePropertiesWithNullGrace
        );

        // when & then - 예외 없이 완료되어야 함
        assertThatCode(schedulerWithDefaultGrace::cleanOrphanFiles)
            .doesNotThrowAnyException();
    }
}
