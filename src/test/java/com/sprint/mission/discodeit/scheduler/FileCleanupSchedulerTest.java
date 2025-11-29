package com.sprint.mission.discodeit.scheduler;

import com.sprint.mission.discodeit.config.properties.S3Properties;
import com.sprint.mission.discodeit.config.properties.StorageProperties;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
@DisplayName("FileCleanupScheduler 통합 테스트 (LocalStack)")
class FileCleanupSchedulerTest {

    private static final String BUCKET_NAME = "test-cleanup-bucket";

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(
        DockerImageName.parse("localstack/localstack:3.5.0")
    ).withServices(S3);

    private FileCleanupScheduler scheduler;
    private S3Client s3Client;
    private BinaryContentRepository mockRepository;

    @BeforeEach
    void setUp() {
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

        // Properties 설정
        S3Properties s3Properties = new S3Properties(
            localStack.getAccessKey(),
            localStack.getSecretKey(),
            localStack.getRegion(),
            BUCKET_NAME,
            Duration.ofMinutes(5)
        );

        StorageProperties storageProperties = new StorageProperties(
            "s3",
            Duration.ofSeconds(2), // 테스트용 짧은 grace period
            null
        );

        // Repository Mock
        mockRepository = mock(BinaryContentRepository.class);

        // Scheduler 생성
        scheduler = new FileCleanupScheduler(
            mockRepository,
            s3Properties,
            storageProperties,
            s3Client
        );
    }

    @AfterEach
    void tearDown() {
        // 버킷 내 모든 객체 삭제
        var listResponse = s3Client.listObjectsV2(
            ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .build()
        );

        if (!listResponse.contents().isEmpty()) {
            s3Client.deleteObjects(DeleteObjectsRequest.builder()
                .bucket(BUCKET_NAME)
                .delete(Delete.builder()
                    .objects(listResponse.contents().stream()
                        .map(obj -> ObjectIdentifier.builder()
                            .key(obj.key())
                            .build())
                        .toList())
                    .build())
                .build());
        }

        s3Client.close();
    }

    @Test
    @DisplayName("고아 파일을 성공적으로 삭제한다")
    void cleanOrphanFiles_DeletesOrphanFiles() throws InterruptedException {
        // given - S3에 고아 파일 업로드 (DB에는 없음)
        UUID orphanId = UUID.randomUUID();
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(orphanId.toString())
                .build(),
            RequestBody.fromString("orphan content")
        );

        // grace period만큼 대기
        Thread.sleep(3000);

        given(mockRepository.findAllByIdIn(anyList()))
            .willReturn(List.of()); // DB에 없음

        // when
        scheduler.cleanOrphanFiles();

        // then - S3에서 삭제되었는지 확인
        assertThat(listAllKeys()).doesNotContain(orphanId.toString());

        then(mockRepository).should().findAllByIdIn(anyList());
    }

    @Test
    @DisplayName("DB에 존재하는 파일은 삭제하지 않는다")
    void cleanOrphanFiles_KeepsExistingFiles() throws Exception {
        // given - S3에 파일 업로드 (DB에도 존재)
        UUID existingId = UUID.randomUUID();
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(existingId.toString())
                .build(),
            RequestBody.fromString("existing content")
        );

        Thread.sleep(3000);

        // DB에 존재하는 것으로 Mock - ID를 existingId로 설정
        BinaryContent binaryContent = new BinaryContent(
            "test.txt", 1024L, "text/plain"
        );
        Field idField = binaryContent.getClass()
            .getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(binaryContent, existingId);

        given(mockRepository.findAllByIdIn(anyList()))
            .willReturn(List.of(binaryContent));

        // when
        scheduler.cleanOrphanFiles();

        // then - S3에 여전히 존재해야 함
        assertThat(listAllKeys()).contains(existingId.toString());
    }

    @Test
    @DisplayName("grace period 이전의 파일은 삭제하지 않는다")
    void cleanOrphanFiles_SkipsRecentFiles() {
        // given - 방금 업로드한 파일
        UUID recentId = UUID.randomUUID();
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(recentId.toString())
                .build(),
            RequestBody.fromString("recent content")
        );

        given(mockRepository.findAllByIdIn(anyList()))
            .willReturn(List.of());

        // when - 대기 없이 바로 실행
        scheduler.cleanOrphanFiles();

        // then - 아직 삭제되지 않아야 함
        assertThat(listAllKeys()).contains(recentId.toString());
    }

    @Test
    @DisplayName("UUID 패턴이 아닌 파일명은 무시한다")
    void cleanOrphanFiles_IgnoresNonUuidKeys() throws InterruptedException {
        // given
        String invalidKey = "not-a-uuid.txt";
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(invalidKey)
                .build(),
            RequestBody.fromString("invalid key content")
        );

        Thread.sleep(3000);

        // when
        scheduler.cleanOrphanFiles();

        // then - UUID가 아닌 파일은 그대로 유지
        assertThat(listAllKeys()).contains(invalidKey);

        // Repository는 호출되지 않아야 함 (UUID 파일이 없으므로)
        then(mockRepository).should(never()).findAllByIdIn(anyList());
    }

    @Test
    @DisplayName("여러 파일 중 고아 파일만 선택적으로 삭제한다")
    void cleanOrphanFiles_MixedScenario() throws InterruptedException {
        // given
        UUID orphan1 = UUID.randomUUID();
        UUID orphan2 = UUID.randomUUID();
        UUID existing = UUID.randomUUID();
        UUID recent = UUID.randomUUID();

        // 고아 파일 2개
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(orphan1.toString())
                .build(),
            RequestBody.fromString("orphan1")
        );
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(orphan2.toString())
                .build(),
            RequestBody.fromString("orphan2")
        );

        // DB에 존재하는 파일
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(existing.toString())
                .build(),
            RequestBody.fromString("existing")
        );

        Thread.sleep(3000);

        // 최근 파일 (grace period 이내)
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(recent.toString())
                .build(),
            RequestBody.fromString("recent")
        );

        // existing만 DB에 존재 - ID를 existing UUID로 설정
        BinaryContent binaryContent = new BinaryContent(
            "existing.txt", 1024L, "text/plain"
        );
        try {
            Field idField = binaryContent.getClass()
                .getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(binaryContent, existing);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        given(mockRepository.findAllByIdIn(anyList()))
            .willReturn(List.of(binaryContent));

        // when
        scheduler.cleanOrphanFiles();

        // then
        List<String> remainingKeys = listAllKeys();
        assertThat(remainingKeys).doesNotContain(
            orphan1.toString(),
            orphan2.toString()
        );
        assertThat(remainingKeys).contains(
            existing.toString(),
            recent.toString()
        );
    }

    @Test
    @DisplayName("빈 버킷에서는 정상 종료한다")
    void cleanOrphanFiles_EmptyBucket() {
        // given - 빈 버킷

        // when
        scheduler.cleanOrphanFiles();

        // then - 예외 없이 종료
        assertThat(listAllKeys()).isEmpty();

        then(mockRepository).should(never()).findAllByIdIn(anyList());
    }

    private List<String> listAllKeys() {
        return s3Client.listObjectsV2(
                ListObjectsV2Request.builder()
                    .bucket(BUCKET_NAME)
                    .build()
            ).contents().stream()
            .map(S3Object::key)
            .toList();
    }
}
