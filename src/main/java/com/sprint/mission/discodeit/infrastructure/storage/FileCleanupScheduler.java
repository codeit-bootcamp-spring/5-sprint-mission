package com.sprint.mission.discodeit.infrastructure.storage;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.common.domain.BaseEntity;
import com.sprint.mission.discodeit.global.config.properties.S3Properties;
import com.sprint.mission.discodeit.global.config.properties.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "s3")
@RequiredArgsConstructor
@Slf4j
public class FileCleanupScheduler {

    private static final int LIST_BATCH_SIZE = 1000;
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    private final BinaryContentRepository binaryContentRepository;
    private final StorageProperties storageProperties;
    private final S3Properties s3Properties;
    private final S3Client s3Client;

    @Scheduled(fixedDelayString = "${discodeit.storage.cleanup-interval:3600000}")
    public void cleanOrphanFiles() {
        log.info("S3 고아 파일 정리 작업 시작");
        String bucket = s3Properties.bucket();

        Duration gracePeriod = storageProperties.orphanGrace();
        Instant threshold = Instant.now().minus(gracePeriod);

        ListObjectsV2Request request = ListObjectsV2Request.builder()
            .bucket(bucket)
            .maxKeys(LIST_BATCH_SIZE)
            .build();

        int totalDeleted = 0;

        for (ListObjectsV2Response page : s3Client.listObjectsV2Paginator(request)) {
            totalDeleted += processBatch(page.contents(), bucket, threshold);
        }

        log.info("S3 고아 파일 정리 완료. 총 삭제: {}건", totalDeleted);
    }

    private int processBatch(List<S3Object> s3Objects, String bucket, Instant threshold) {
        if (s3Objects.isEmpty()) {
            return 0;
        }

        List<UUID> candidateIds = s3Objects.stream()
            .filter(obj -> UUID_PATTERN.matcher(obj.key()).matches())
            .filter(obj -> obj.lastModified().isBefore(threshold))
            .map(obj -> UUID.fromString(obj.key()))
            .toList();

        if (candidateIds.isEmpty()) {
            return 0;
        }

        Set<UUID> existingIds = binaryContentRepository.findAllById(candidateIds).stream()
            .map(BaseEntity::getId)
            .collect(Collectors.toSet());

        List<ObjectIdentifier> orphansToDelete = candidateIds.stream()
            .filter(id -> !existingIds.contains(id))
            .map(id -> ObjectIdentifier.builder().key(id.toString()).build())
            .toList();

        return deleteObjectsFromS3(bucket, orphansToDelete);
    }

    private int deleteObjectsFromS3(String bucket, List<ObjectIdentifier> objects) {
        if (objects.isEmpty()) {
            return 0;
        }

        try {
            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(Delete.builder().objects(objects).build())
                .build();

            DeleteObjectsResponse response = s3Client.deleteObjects(deleteRequest);
            int deletedCount = response.deleted().size();
            int errorCount = response.errors().size();

            if (errorCount > 0) {
                log.warn("배치 삭제 일부 실패: 요청 {}건 -> 성공 {}건, 실패 {}건",
                    objects.size(), deletedCount, errorCount);

                response.errors().forEach(error ->
                    log.warn("삭제 실패: key={}, code={}, message={}",
                        error.key(), error.code(), error.message())
                );
            } else {
                log.info("배치 삭제 수행: 요청 {}건 -> 성공 {}건", objects.size(), deletedCount);
            }

            return deletedCount;
        } catch (S3Exception e) {
            log.error("S3 객체 삭제 실패: bucket={}, objectCount={}",
                bucket, objects.size(), e);
            return 0;
        }
    }
}
