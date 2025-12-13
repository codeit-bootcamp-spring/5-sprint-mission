package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStorage;
import com.sprint.mission.discodeit.infrastructure.storage.FileCleanupScheduler;
import com.sprint.mission.discodeit.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "discodeit.storage.orphan-grace=0s"
})
@DisplayName("파일 스토리지 통합 테스트")
class BinaryContentStorageProcessorIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private BinaryContentStorage binaryContentStorage;

    @Autowired
    private FileCleanupScheduler fileCleanupScheduler;

    @Autowired
    private S3Client s3Client;

    @Value("${discodeit.s3.bucket}")
    private String bucket;

    @BeforeEach
    void setUp() {
        clearS3Bucket();
    }

    @AfterEach
    void tearDown() {
        binaryContentRepository.deleteAll();
        clearS3Bucket();
    }

    @Test
    @DisplayName("S3에 파일이 있지만 DB에 없는 경우 스케줄러가 파일을 삭제한다")
    void cleanOrphanFiles_whenFileExistsOnlyInS3_deletesFile() {
        // given: S3에만 파일 업로드 (DB에는 저장하지 않음 - 업로드 중 이탈 시뮬레이션)
        UUID orphanFileId = UUID.randomUUID();
        byte[] fileContent = "orphan file content".getBytes();
        binaryContentStorage.put(orphanFileId, fileContent);

        // S3에 파일이 존재하는지 확인
        assertThat(listS3ObjectKeys()).contains(orphanFileId.toString());

        // when: 스케줄러 실행
        fileCleanupScheduler.cleanOrphanFiles();

        // then: S3에서 파일이 삭제됨
        assertThat(listS3ObjectKeys()).doesNotContain(orphanFileId.toString());
    }

    @Test
    @DisplayName("DB에 BinaryContent가 있는 파일은 스케줄러가 삭제하지 않는다")
    void cleanOrphanFiles_whenFileExistsInDb_doesNotDelete() {
        // given: DB에 BinaryContent 저장
        BinaryContent binaryContent = new BinaryContent("test.txt", 100L, "text/plain");
        binaryContentRepository.save(binaryContent);
        UUID fileId = binaryContent.getId();

        // S3에 파일 업로드
        byte[] fileContent = "valid file content".getBytes();
        binaryContentStorage.put(fileId, fileContent);

        // S3에 파일이 존재하는지 확인
        assertThat(listS3ObjectKeys()).contains(fileId.toString());

        // when: 스케줄러 실행
        fileCleanupScheduler.cleanOrphanFiles();

        // then: S3에 파일이 여전히 존재함 (DB에 있으므로 삭제되지 않음)
        assertThat(listS3ObjectKeys()).contains(fileId.toString());
        // DB에도 여전히 존재
        assertThat(binaryContentRepository.findById(fileId)).isPresent();
    }

    @Test
    @DisplayName("여러 고아 파일이 있을 때 모두 삭제된다")
    void cleanOrphanFiles_multipleOrphans_deletesAll() {
        // given: 여러 고아 파일 업로드
        UUID orphan1 = UUID.randomUUID();
        UUID orphan2 = UUID.randomUUID();
        UUID orphan3 = UUID.randomUUID();

        binaryContentStorage.put(orphan1, "content1".getBytes());
        binaryContentStorage.put(orphan2, "content2".getBytes());
        binaryContentStorage.put(orphan3, "content3".getBytes());

        // DB에는 하나만 저장
        BinaryContent validContent = new BinaryContent("valid.txt", 50L, "text/plain");
        binaryContentRepository.save(validContent);
        binaryContentStorage.put(validContent.getId(), "valid content".getBytes());

        // when: 스케줄러 실행
        fileCleanupScheduler.cleanOrphanFiles();

        // then: 고아 파일들만 삭제됨
        List<String> remainingKeys = listS3ObjectKeys();
        assertThat(remainingKeys)
            .doesNotContain(orphan1.toString(), orphan2.toString(), orphan3.toString())
            .contains(validContent.getId().toString());
    }

    private List<String> listS3ObjectKeys() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
            .bucket(bucket)
            .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        return response.contents().stream()
            .map(S3Object::key)
            .toList();
    }

    private void clearS3Bucket() {
        List<ObjectIdentifier> objects = listS3ObjectKeys().stream()
            .map(key -> ObjectIdentifier.builder().key(key).build())
            .toList();

        if (!objects.isEmpty()) {
            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(Delete.builder().objects(objects).build())
                .build();
            s3Client.deleteObjects(deleteRequest);
        }
    }
}
