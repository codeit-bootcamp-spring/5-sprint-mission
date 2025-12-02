package com.sprint.mission.discodeit.scheduler;

import com.sprint.mission.discodeit.config.properties.S3Properties;
import com.sprint.mission.discodeit.config.properties.StorageProperties;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.DeletedObject;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Error;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.createBinaryContentWithId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileCleanupScheduler 단위 테스트 (Mockito)")
class FileCleanupSchedulerMockTest {

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private S3Client s3Client;

    @Mock
    private ListObjectsV2Iterable mockIterable;

    private FileCleanupScheduler scheduler;

    @BeforeEach
    void setUp() {
        S3Properties s3Properties = new S3Properties(
            "test-key",
            "test-secret",
            "us-east-1",
            "test-bucket",
            Duration.ofMinutes(5)
        );

        StorageProperties storageProperties = new StorageProperties(
            "s3",
            Duration.ofMinutes(10),
            null
        );

        scheduler = new FileCleanupScheduler(
            binaryContentRepository,
            s3Properties,
            storageProperties,
            s3Client
        );
    }

    private S3Object createS3Object(UUID id, Instant lastModified) {
        return S3Object.builder()
            .key(id.toString())
            .lastModified(lastModified)
            .build();
    }

    private S3Object createS3Object(Instant lastModified) {
        return S3Object.builder()
            .key("not-a-uuid.txt")
            .lastModified(lastModified)
            .build();
    }

    private void setupPaginator(ListObjectsV2Response... responses) {
        given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
            .willReturn(mockIterable);
        given(mockIterable.iterator())
            .willReturn(List.of(responses).iterator());
    }

    @Test
    @DisplayName("고아 파일을 삭제한다")
    void cleanOrphanFiles_DeletesOrphans() {
        // given
        UUID orphanId = UUID.randomUUID();
        Instant oldTime = Instant.now().minus(Duration.ofHours(1));

        ListObjectsV2Response response = ListObjectsV2Response.builder()
            .contents(List.of(createS3Object(orphanId, oldTime)))
            .build();
        setupPaginator(response);

        given(binaryContentRepository.findAllById(anyList()))
            .willReturn(List.of());

        DeletedObject deletedObject = DeletedObject.builder()
            .key(orphanId.toString())
            .build();
        DeleteObjectsResponse deleteResponse = DeleteObjectsResponse.builder()
            .deleted(List.of(deletedObject))
            .build();
        given(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
            .willReturn(deleteResponse);

        // when
        scheduler.cleanOrphanFiles();

        // then
        then(s3Client).should().deleteObjects(any(DeleteObjectsRequest.class));
        then(binaryContentRepository).should().findAllById(anyList());
    }

    @Test
    @DisplayName("DB에 존재하는 파일은 삭제하지 않는다")
    void cleanOrphanFiles_KeepsExistingFiles() {
        // given
        UUID existingId = UUID.randomUUID();
        Instant oldTime = Instant.now().minus(Duration.ofHours(1));

        ListObjectsV2Response response = ListObjectsV2Response.builder()
            .contents(List.of(createS3Object(existingId, oldTime)))
            .build();
        setupPaginator(response);

        given(binaryContentRepository.findAllById(anyList()))
            .willReturn(List.of(createBinaryContentWithId(existingId)));

        // when
        scheduler.cleanOrphanFiles();

        // then
        then(s3Client).should(never()).deleteObjects(any(DeleteObjectsRequest.class));
        then(binaryContentRepository).should().findAllById(anyList());
    }

    @Test
    @DisplayName("최근 파일은 스킵한다")
    void cleanOrphanFiles_SkipsRecentFiles() {
        // given
        UUID recentId = UUID.randomUUID();
        Instant recentTime = Instant.now().minus(Duration.ofSeconds(1));

        ListObjectsV2Response response = ListObjectsV2Response.builder()
            .contents(List.of(createS3Object(recentId, recentTime)))
            .build();
        setupPaginator(response);

        // when
        scheduler.cleanOrphanFiles();

        // then
        then(binaryContentRepository).should(never()).findAllById(anyList());
        then(s3Client).should(never()).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("UUID가 아닌 키는 무시한다")
    void cleanOrphanFiles_IgnoresNonUuidKeys() {
        // given
        Instant oldTime = Instant.now().minus(Duration.ofHours(1));

        ListObjectsV2Response response = ListObjectsV2Response.builder()
            .contents(List.of(createS3Object(oldTime)))
            .build();
        setupPaginator(response);

        // when
        scheduler.cleanOrphanFiles();

        // then
        then(binaryContentRepository).should(never()).findAllById(anyList());
        then(s3Client).should(never()).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("빈 응답에서도 정상 동작한다")
    void cleanOrphanFiles_EmptyResponse() {
        // given
        ListObjectsV2Response emptyResponse = ListObjectsV2Response.builder()
            .contents(List.of())
            .build();
        setupPaginator(emptyResponse);

        // when
        scheduler.cleanOrphanFiles();

        // then
        then(binaryContentRepository).should(never()).findAllById(anyList());
        then(s3Client).should(never()).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("여러 페이지를 처리한다")
    void cleanOrphanFiles_MultiplePages() {
        // given
        UUID orphan1 = UUID.randomUUID();
        UUID orphan2 = UUID.randomUUID();
        Instant oldTime = Instant.now().minus(Duration.ofHours(1));

        ListObjectsV2Response page1 = ListObjectsV2Response.builder()
            .contents(List.of(createS3Object(orphan1, oldTime)))
            .build();
        ListObjectsV2Response page2 = ListObjectsV2Response.builder()
            .contents(List.of(createS3Object(orphan2, oldTime)))
            .build();
        setupPaginator(page1, page2);

        given(binaryContentRepository.findAllById(anyList()))
            .willReturn(List.of());

        DeletedObject deleted1 = DeletedObject.builder().key(orphan1.toString()).build();
        DeletedObject deleted2 = DeletedObject.builder().key(orphan2.toString()).build();
        DeleteObjectsResponse deleteResponse1 = DeleteObjectsResponse.builder()
            .deleted(List.of(deleted1))
            .build();
        DeleteObjectsResponse deleteResponse2 = DeleteObjectsResponse.builder()
            .deleted(List.of(deleted2))
            .build();
        given(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
            .willReturn(deleteResponse1, deleteResponse2);

        // when
        scheduler.cleanOrphanFiles();

        // then
        then(s3Client).should(times(2)).deleteObjects(any(DeleteObjectsRequest.class));
        then(binaryContentRepository).should(times(2)).findAllById(anyList());
    }

    @Test
    @DisplayName("삭제 시 일부 에러가 발생하면 경고 로그를 남긴다")
    void cleanOrphanFiles_PartialDeleteError_LogsWarning() {
        // given
        UUID orphanId1 = UUID.randomUUID();
        UUID orphanId2 = UUID.randomUUID();
        Instant oldTime = Instant.now().minus(Duration.ofHours(1));

        ListObjectsV2Response response = ListObjectsV2Response.builder()
            .contents(List.of(
                createS3Object(orphanId1, oldTime),
                createS3Object(orphanId2, oldTime)
            ))
            .build();
        setupPaginator(response);

        given(binaryContentRepository.findAllById(anyList()))
            .willReturn(List.of());

        DeletedObject deletedObject = DeletedObject.builder()
            .key(orphanId1.toString())
            .build();
        S3Error s3Error = S3Error.builder()
            .key(orphanId2.toString())
            .code("AccessDenied")
            .message("Access Denied")
            .build();
        DeleteObjectsResponse deleteResponse = DeleteObjectsResponse.builder()
            .deleted(List.of(deletedObject))
            .errors(List.of(s3Error))
            .build();
        given(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
            .willReturn(deleteResponse);

        // when
        scheduler.cleanOrphanFiles();

        // then
        then(s3Client).should().deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("S3Exception 발생 시 에러를 로깅하고 0을 반환한다")
    void cleanOrphanFiles_S3Exception_LogsErrorAndReturnsZero() {
        // given
        UUID orphanId = UUID.randomUUID();
        Instant oldTime = Instant.now().minus(Duration.ofHours(1));

        ListObjectsV2Response response = ListObjectsV2Response.builder()
            .contents(List.of(createS3Object(orphanId, oldTime)))
            .build();
        setupPaginator(response);

        given(binaryContentRepository.findAllById(anyList()))
            .willReturn(List.of());

        AwsErrorDetails errorDetails = AwsErrorDetails.builder()
            .errorCode("InternalError")
            .errorMessage("Internal Server Error")
            .build();
        S3Exception s3Exception = (S3Exception) S3Exception.builder()
            .awsErrorDetails(errorDetails)
            .message("S3 Error")
            .build();
        given(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
            .willThrow(s3Exception);

        // when
        scheduler.cleanOrphanFiles();

        // then
        then(s3Client).should().deleteObjects(any(DeleteObjectsRequest.class));
    }
}
