package com.sprint.mission.discodeit.infrastructure.storage;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.global.config.properties.S3Properties;
import com.sprint.mission.discodeit.global.config.properties.StorageProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.DeletedObject;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileCleanupScheduler 단위 테스트")
class FileCleanupSchedulerTest {

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private S3Client s3Client;

    private FileCleanupScheduler fileCleanupScheduler;

    private static final String TEST_BUCKET = "test-bucket";
    private static final Duration ORPHAN_GRACE = Duration.ofHours(1);

    @BeforeEach
    void setUp() {
        StorageProperties storageProperties = new StorageProperties(
            ORPHAN_GRACE, 3600000L, new StorageProperties.Retry(3, 2000L, 3.0)
        );
        S3Properties s3Properties = new S3Properties(
            "accessKey", "secretKey", "us-east-1", TEST_BUCKET, null, Duration.ofMinutes(10)
        );

        fileCleanupScheduler = new FileCleanupScheduler(
            binaryContentRepository, storageProperties, s3Properties, s3Client
        );
    }

    @SuppressWarnings("unchecked")
    private ListObjectsV2Iterable mockPaginator(ListObjectsV2Response... responses) {
        ListObjectsV2Iterable paginator = mock(ListObjectsV2Iterable.class);
        Iterator<ListObjectsV2Response> iterator = mock(Iterator.class);

        if (responses.length == 0) {
            given(iterator.hasNext()).willReturn(false);
        } else if (responses.length == 1) {
            given(iterator.hasNext()).willReturn(true, false);
            given(iterator.next()).willReturn(responses[0]);
        } else {
            Boolean[] hasNextValues = new Boolean[responses.length + 1];
            for (int i = 0; i < responses.length; i++) {
                hasNextValues[i] = true;
            }
            hasNextValues[responses.length] = false;

            given(iterator.hasNext()).willReturn(hasNextValues[0],
                java.util.Arrays.copyOfRange(hasNextValues, 1, hasNextValues.length));

            given(iterator.next()).willReturn(responses[0],
                java.util.Arrays.copyOfRange(responses, 1, responses.length));
        }

        given(paginator.iterator()).willReturn(iterator);
        return paginator;
    }

    @Nested
    @DisplayName("cleanOrphanFiles")
    class CleanOrphanFiles {

        @Test
        @DisplayName("S3에 객체가 없으면 삭제 작업을 수행하지 않음")
        void cleanOrphanFiles_noObjects_doesNotDelete() {
            // given
            ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(Collections.emptyList())
                .build();
            ListObjectsV2Iterable paginator = mockPaginator(response);

            given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .willReturn(paginator);

            // when
            fileCleanupScheduler.cleanOrphanFiles();

            // then
            then(s3Client).should(never()).deleteObjects(any(DeleteObjectsRequest.class));
        }

        @Test
        @DisplayName("유효한 UUID가 아닌 객체는 삭제 대상에서 제외")
        void cleanOrphanFiles_nonUuidKeys_excludedFromDeletion() {
            // given
            Instant oldTime = Instant.now().minus(Duration.ofHours(2));
            S3Object invalidObject = S3Object.builder()
                .key("invalid-key-not-uuid")
                .lastModified(oldTime)
                .build();

            ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(List.of(invalidObject))
                .build();
            ListObjectsV2Iterable paginator = mockPaginator(response);

            given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .willReturn(paginator);

            // when
            fileCleanupScheduler.cleanOrphanFiles();

            // then
            then(s3Client).should(never()).deleteObjects(any(DeleteObjectsRequest.class));
        }

        @Test
        @DisplayName("grace period 내의 객체는 삭제 대상에서 제외")
        void cleanOrphanFiles_recentObjects_excludedFromDeletion() {
            // given
            UUID objectId = UUID.randomUUID();
            Instant recentTime = Instant.now().minus(Duration.ofMinutes(30));
            S3Object recentObject = S3Object.builder()
                .key(objectId.toString())
                .lastModified(recentTime)
                .build();

            ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(List.of(recentObject))
                .build();
            ListObjectsV2Iterable paginator = mockPaginator(response);

            given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .willReturn(paginator);

            // when
            fileCleanupScheduler.cleanOrphanFiles();

            // then
            then(s3Client).should(never()).deleteObjects(any(DeleteObjectsRequest.class));
        }

        @Test
        @DisplayName("DB에 존재하는 객체는 삭제 대상에서 제외")
        void cleanOrphanFiles_existingInDb_excludedFromDeletion() {
            // given
            UUID objectId = UUID.randomUUID();
            Instant oldTime = Instant.now().minus(Duration.ofHours(2));
            S3Object s3Object = S3Object.builder()
                .key(objectId.toString())
                .lastModified(oldTime)
                .build();

            BinaryContent existingContent = new BinaryContent("file.png", 1024L, "image/png");
            ReflectionTestUtils.setField(existingContent, "id", objectId);

            ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(List.of(s3Object))
                .build();
            ListObjectsV2Iterable paginator = mockPaginator(response);

            given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .willReturn(paginator);
            given(binaryContentRepository.findAllById(List.of(objectId)))
                .willReturn(List.of(existingContent));

            // when
            fileCleanupScheduler.cleanOrphanFiles();

            // then
            then(s3Client).should(never()).deleteObjects(any(DeleteObjectsRequest.class));
        }

        @Test
        @DisplayName("고아 파일 삭제 성공")
        void cleanOrphanFiles_orphanFiles_deletedSuccessfully() {
            // given
            UUID orphanId = UUID.randomUUID();
            Instant oldTime = Instant.now().minus(Duration.ofHours(2));
            S3Object orphanObject = S3Object.builder()
                .key(orphanId.toString())
                .lastModified(oldTime)
                .build();

            ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(List.of(orphanObject))
                .build();
            ListObjectsV2Iterable paginator = mockPaginator(response);

            DeleteObjectsResponse deleteResponse = DeleteObjectsResponse.builder()
                .deleted(List.of(DeletedObject.builder().key(orphanId.toString()).build()))
                .errors(Collections.emptyList())
                .build();

            given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .willReturn(paginator);
            given(binaryContentRepository.findAllById(List.of(orphanId)))
                .willReturn(Collections.emptyList());
            given(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
                .willReturn(deleteResponse);

            // when
            fileCleanupScheduler.cleanOrphanFiles();

            // then
            ArgumentCaptor<DeleteObjectsRequest> captor =
                ArgumentCaptor.forClass(DeleteObjectsRequest.class);
            then(s3Client).should().deleteObjects(captor.capture());

            DeleteObjectsRequest request = captor.getValue();
            assertThat(request.bucket()).isEqualTo(TEST_BUCKET);
            assertThat(request.delete().objects()).hasSize(1);
            assertThat(request.delete().objects().get(0).key()).isEqualTo(orphanId.toString());
        }

        @Test
        @DisplayName("다수의 고아 파일 삭제")
        void cleanOrphanFiles_multipleOrphanFiles_allDeleted() {
            // given
            UUID orphanId1 = UUID.randomUUID();
            UUID orphanId2 = UUID.randomUUID();
            Instant oldTime = Instant.now().minus(Duration.ofHours(2));

            S3Object orphan1 = S3Object.builder()
                .key(orphanId1.toString())
                .lastModified(oldTime)
                .build();
            S3Object orphan2 = S3Object.builder()
                .key(orphanId2.toString())
                .lastModified(oldTime)
                .build();

            ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(List.of(orphan1, orphan2))
                .build();
            ListObjectsV2Iterable paginator = mockPaginator(response);

            DeleteObjectsResponse deleteResponse = DeleteObjectsResponse.builder()
                .deleted(List.of(
                    DeletedObject.builder().key(orphanId1.toString()).build(),
                    DeletedObject.builder().key(orphanId2.toString()).build()
                ))
                .errors(Collections.emptyList())
                .build();

            given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .willReturn(paginator);
            given(binaryContentRepository.findAllById(any()))
                .willReturn(Collections.emptyList());
            given(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
                .willReturn(deleteResponse);

            // when
            fileCleanupScheduler.cleanOrphanFiles();

            // then
            ArgumentCaptor<DeleteObjectsRequest> captor =
                ArgumentCaptor.forClass(DeleteObjectsRequest.class);
            then(s3Client).should().deleteObjects(captor.capture());

            assertThat(captor.getValue().delete().objects()).hasSize(2);
        }

        @Test
        @DisplayName("S3 삭제 중 예외 발생 시 예외를 던지지 않음")
        void cleanOrphanFiles_s3Exception_doesNotThrow() {
            // given
            UUID orphanId = UUID.randomUUID();
            Instant oldTime = Instant.now().minus(Duration.ofHours(2));
            S3Object orphanObject = S3Object.builder()
                .key(orphanId.toString())
                .lastModified(oldTime)
                .build();

            ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(List.of(orphanObject))
                .build();
            ListObjectsV2Iterable paginator = mockPaginator(response);

            given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .willReturn(paginator);
            given(binaryContentRepository.findAllById(any()))
                .willReturn(Collections.emptyList());
            given(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
                .willThrow(S3Exception.builder().message("S3 error").build());

            // when & then (no exception thrown)
            fileCleanupScheduler.cleanOrphanFiles();
        }

        @Test
        @DisplayName("S3 삭제 응답에 일부 에러가 포함된 경우 성공 건수만 반환")
        void cleanOrphanFiles_partialDeleteFailure_returnsSuccessCount() {
            // given
            UUID orphanId1 = UUID.randomUUID();
            UUID orphanId2 = UUID.randomUUID();
            Instant oldTime = Instant.now().minus(Duration.ofHours(2));

            S3Object orphan1 = S3Object.builder()
                .key(orphanId1.toString())
                .lastModified(oldTime)
                .build();
            S3Object orphan2 = S3Object.builder()
                .key(orphanId2.toString())
                .lastModified(oldTime)
                .build();

            ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(List.of(orphan1, orphan2))
                .build();
            ListObjectsV2Iterable paginator = mockPaginator(response);

            software.amazon.awssdk.services.s3.model.S3Error s3Error =
                software.amazon.awssdk.services.s3.model.S3Error.builder()
                    .key(orphanId2.toString())
                    .code("AccessDenied")
                    .message("Access Denied")
                    .build();

            DeleteObjectsResponse deleteResponse = DeleteObjectsResponse.builder()
                .deleted(List.of(DeletedObject.builder().key(orphanId1.toString()).build()))
                .errors(List.of(s3Error))
                .build();

            given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .willReturn(paginator);
            given(binaryContentRepository.findAllById(any()))
                .willReturn(Collections.emptyList());
            given(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
                .willReturn(deleteResponse);

            // when
            fileCleanupScheduler.cleanOrphanFiles();

            // then
            then(s3Client).should().deleteObjects(any(DeleteObjectsRequest.class));
        }

        @Test
        @DisplayName("S3 목록 조회 중 예외 발생 시 예외를 전파함")
        void cleanOrphanFiles_listObjectsException_throwsException() {
            // given
            given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .willThrow(S3Exception.builder().message("Access Denied").build());

            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(
                S3Exception.class,
                () -> fileCleanupScheduler.cleanOrphanFiles()
            );
        }

        @Test
        @DisplayName("혼합된 객체들 중 고아 파일만 삭제")
        void cleanOrphanFiles_mixedObjects_onlyOrphansDeleted() {
            // given
            UUID orphanId = UUID.randomUUID();
            UUID existingId = UUID.randomUUID();
            UUID recentId = UUID.randomUUID();
            Instant oldTime = Instant.now().minus(Duration.ofHours(2));
            Instant recentTime = Instant.now().minus(Duration.ofMinutes(30));

            S3Object orphanObject = S3Object.builder()
                .key(orphanId.toString())
                .lastModified(oldTime)
                .build();
            S3Object existingObject = S3Object.builder()
                .key(existingId.toString())
                .lastModified(oldTime)
                .build();
            S3Object recentObject = S3Object.builder()
                .key(recentId.toString())
                .lastModified(recentTime)
                .build();
            S3Object invalidObject = S3Object.builder()
                .key("not-a-uuid")
                .lastModified(oldTime)
                .build();

            BinaryContent existingContent = new BinaryContent("file.png", 1024L, "image/png");
            ReflectionTestUtils.setField(existingContent, "id", existingId);

            ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(List.of(orphanObject, existingObject, recentObject, invalidObject))
                .build();
            ListObjectsV2Iterable paginator = mockPaginator(response);

            DeleteObjectsResponse deleteResponse = DeleteObjectsResponse.builder()
                .deleted(List.of(DeletedObject.builder().key(orphanId.toString()).build()))
                .errors(Collections.emptyList())
                .build();

            given(s3Client.listObjectsV2Paginator(any(ListObjectsV2Request.class)))
                .willReturn(paginator);
            given(binaryContentRepository.findAllById(any()))
                .willReturn(List.of(existingContent));
            given(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
                .willReturn(deleteResponse);

            // when
            fileCleanupScheduler.cleanOrphanFiles();

            // then
            ArgumentCaptor<DeleteObjectsRequest> captor =
                ArgumentCaptor.forClass(DeleteObjectsRequest.class);
            then(s3Client).should().deleteObjects(captor.capture());

            assertThat(captor.getValue().delete().objects()).hasSize(1);
            assertThat(captor.getValue().delete().objects().get(0).key())
                .isEqualTo(orphanId.toString());
        }
    }
}
