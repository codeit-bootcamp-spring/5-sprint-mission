package com.sprint.mission.discodeit.scheduler;

import com.sprint.mission.discodeit.config.properties.S3Properties;
import com.sprint.mission.discodeit.config.properties.StorageProperties;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "s3")
@RequiredArgsConstructor
@Slf4j
public class FileCleanupScheduler {

    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    private final BinaryContentRepository binaryContentRepository;
    private final S3Properties s3Properties;
    private final StorageProperties storageProperties;

    private S3Client getS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
            s3Properties.accessKey(),
            s3Properties.secretKey()
        );

        return S3Client.builder()
            .region(Region.of(s3Properties.region()))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }

    @Scheduled(fixedDelay = 3600_000)
    public void cleanOrphanFiles() {
        log.info("S3 고아 파일 정리 작업 시작");

        Duration orphanGrace = storageProperties.local() != null
            && storageProperties.local().orphanGrace() != null
            ? storageProperties.local().orphanGrace()
            : Duration.ofMinutes(10);

        Instant threshold = Instant.now().minus(orphanGrace);
        int deletedCount = 0;
        List<ObjectIdentifier> orphanObjects = new ArrayList<>();

        try (S3Client s3Client = getS3Client()) {
            String bucket = s3Properties.bucket();
            String continuationToken = null;

            do {
                ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .maxKeys(1000);

                if (continuationToken != null) {
                    requestBuilder.continuationToken(continuationToken);
                }

                ListObjectsV2Response response = s3Client.listObjectsV2(requestBuilder.build());

                for (S3Object s3Object : response.contents()) {
                    try {
                        String key = s3Object.key();

                        if (!UUID_PATTERN.matcher(key).matches()) {
                            log.debug("유효하지 않은 UUID 형식의 키 발견, 건너뜀: {}", key);
                            continue;
                        }

                        if (s3Object.lastModified().isAfter(threshold)) {
                            log.trace("최근 파일이므로 건너뜀: key={}, lastModified={}",
                                key, s3Object.lastModified());
                            continue;
                        }

                        UUID id = UUID.fromString(key);

                        boolean existsInDb = binaryContentRepository.existsById(id);

                        if (!existsInDb) {
                            log.info("고아 파일 발견: key={}, size={}, lastModified={}",
                                key, s3Object.size(), s3Object.lastModified());

                            orphanObjects.add(ObjectIdentifier.builder()
                                .key(key)
                                .build());

                            if (orphanObjects.size() >= 1000) {
                                int batchDeleted = deleteOrphanObjects(s3Client, bucket, orphanObjects);
                                deletedCount += batchDeleted;
                                orphanObjects.clear();
                            }
                        } else {
                            log.trace("DB에 존재하는 파일, 보존: key={}", key);
                        }
                    } catch (Exception e) {
                        log.error("객체 처리 중 오류 발생: key={}", s3Object.key(), e);
                    }
                }

                continuationToken = response.nextContinuationToken();
            } while (continuationToken != null);

            if (!orphanObjects.isEmpty()) {
                int batchDeleted = deleteOrphanObjects(s3Client, bucket, orphanObjects);
                deletedCount += batchDeleted;
            }

            if (deletedCount > 0) {
                log.info("S3 고아 파일 정리 완료. 삭제: {}건", deletedCount);
            } else {
                log.info("S3 고아 파일 정리 완료. 삭제된 파일이 없습니다.");
            }
        } catch (Exception e) {
            log.error("S3 고아 파일 정리 작업 중 오류 발생", e);
        }
    }

    private int deleteOrphanObjects(S3Client s3Client, String bucket, List<ObjectIdentifier> objects) {
        if (objects.isEmpty()) {
            return 0;
        }

        try {
            Delete delete = Delete.builder()
                .objects(objects)
                .quiet(false)
                .build();

            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(delete)
                .build();

            var response = s3Client.deleteObjects(deleteRequest);

            int deletedCount = response.deleted().size();
            int errorCount = response.errors().size();

            if (errorCount > 0) {
                log.warn("S3 객체 삭제 중 일부 오류 발생. 성공: {}, 실패: {}",
                    deletedCount, errorCount);

                response.errors().forEach(error ->
                    log.error("삭제 실패: key={}, code={}, message={}",
                        error.key(), error.code(), error.message())
                );
            } else {
                log.debug("S3 객체 배치 삭제 성공: {}건", deletedCount);
            }

            return deletedCount;
        } catch (Exception e) {
            log.error("S3 객체 배치 삭제 실패: 객체 수={}", objects.size(), e);
            return 0;
        }
    }
}
