package com.sprint.mission.discodeit.scheduler;

import com.sprint.mission.discodeit.config.properties.S3Properties;
import com.sprint.mission.discodeit.config.properties.StorageProperties;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.DeletedObject;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

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

    @Test
    @DisplayName("고아 파일을 삭제한다")
    void cleanOrphanFiles_DeletesOrphans() {
        // given
        UUID orphanId = UUID.randomUUID();
        Instant oldTime = Instant.now().minus(Duration.ofHours(1));

        S3Object s3Object = S3Object.builder()
            .key(orphanId.toString())
            .lastModified(oldTime)
            .build();

        ListObjectsV2Response response = ListObjectsV2Response.builder()
            .contents(List.of(s3Object))
            .build();

        given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
            .willReturn(mockIterable);
        given(mockIterable.iterator())
            .willReturn(List.of(response).iterator());

        given(binaryContentRepository.findAllByIdIn(anyList()))
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
        then(binaryContentRepository).should().findAllByIdIn(anyList());
    }

    @Test
    @DisplayName("DB에 존재하는 파일은 삭제하지 않는다")
    void cleanOrphanFiles_KeepsExistingFiles() throws Exception {
        // given
        UUID existingId = UUID.randomUUID();
        Instant oldTime = Instant.now().minus(Duration.ofHours(1));

        S3Object s3Object = S3Object.builder()
            .key(existingId.toString())
            .lastModified(oldTime)
            .build();

        ListObjectsV2Response response = ListObjectsV2Response.builder()
            .contents(List.of(s3Object))
            .build();

        given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
            .willReturn(mockIterable);
        given(mockIterable.iterator())
            .willReturn(List.of(response).iterator());

        // BinaryContent의 ID를 existingId로 설정 (reflection 사용)
        BinaryContent binaryContent = new BinaryContent(
            "test.txt", 1024L, "text/plain"
        );
        Field idField = binaryContent.getClass()
            .getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(binaryContent, existingId);

        given(binaryContentRepository.findAllByIdIn(anyList()))
            .willReturn(List.of(binaryContent));

        // when
        scheduler.cleanOrphanFiles();

        // then
        then(s3Client).should(never()).deleteObjects(any(DeleteObjectsRequest.class));
        then(binaryContentRepository).should().findAllByIdIn(anyList());
    }

    @Test
    @DisplayName("최근 파일은 스킵한다")
    void cleanOrphanFiles_SkipsRecentFiles() {
        // given
        UUID recentId = UUID.randomUUID();
        Instant recentTime = Instant.now().minus(Duration.ofSeconds(1));

        S3Object s3Object = S3Object.builder()
            .key(recentId.toString())
            .lastModified(recentTime)
            .build();

        ListObjectsV2Response response = ListObjectsV2Response.builder()
            .contents(List.of(s3Object))
            .build();

        given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
            .willReturn(mockIterable);
        given(mockIterable.iterator())
            .willReturn(List.of(response).iterator());

        // when
        scheduler.cleanOrphanFiles();

        // then
        then(binaryContentRepository).should(never()).findAllByIdIn(anyList());
        then(s3Client).should(never()).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("UUID가 아닌 키는 무시한다")
    void cleanOrphanFiles_IgnoresNonUuidKeys() {
        // given
        Instant oldTime = Instant.now().minus(Duration.ofHours(1));

        S3Object s3Object = S3Object.builder()
            .key("not-a-uuid.txt")
            .lastModified(oldTime)
            .build();

        ListObjectsV2Response response = ListObjectsV2Response.builder()
            .contents(List.of(s3Object))
            .build();

        given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
            .willReturn(mockIterable);
        given(mockIterable.iterator())
            .willReturn(List.of(response).iterator());

        // when
        scheduler.cleanOrphanFiles();

        // then
        then(binaryContentRepository).should(never()).findAllByIdIn(anyList());
        then(s3Client).should(never()).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("빈 응답에서도 정상 동작한다")
    void cleanOrphanFiles_EmptyResponse() {
        // given
        ListObjectsV2Response emptyResponse = ListObjectsV2Response.builder()
            .contents(List.of())
            .build();

        given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
            .willReturn(mockIterable);
        given(mockIterable.iterator())
            .willReturn(List.of(emptyResponse).iterator());

        // when
        scheduler.cleanOrphanFiles();

        // then
        then(binaryContentRepository).should(never()).findAllByIdIn(anyList());
        then(s3Client).should(never()).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    @DisplayName("여러 페이지를 처리한다")
    void cleanOrphanFiles_MultiplePages() {
        // given
        UUID orphan1 = UUID.randomUUID();
        UUID orphan2 = UUID.randomUUID();
        Instant oldTime = Instant.now().minus(Duration.ofHours(1));

        S3Object s3Object1 = S3Object.builder()
            .key(orphan1.toString())
            .lastModified(oldTime)
            .build();

        S3Object s3Object2 = S3Object.builder()
            .key(orphan2.toString())
            .lastModified(oldTime)
            .build();

        ListObjectsV2Response page1 = ListObjectsV2Response.builder()
            .contents(List.of(s3Object1))
            .build();

        ListObjectsV2Response page2 = ListObjectsV2Response.builder()
            .contents(List.of(s3Object2))
            .build();

        given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
            .willReturn(mockIterable);
        given(mockIterable.iterator())
            .willReturn(List.of(page1, page2).iterator());

        given(binaryContentRepository.findAllByIdIn(anyList()))
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
        // 페이지별로 배치 삭제가 수행되므로 2번 호출됨
        then(s3Client).should(times(2)).deleteObjects(any(DeleteObjectsRequest.class));
        then(binaryContentRepository).should(times(2)).findAllByIdIn(anyList());
    }
}
