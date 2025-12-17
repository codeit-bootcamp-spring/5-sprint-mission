package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.FileIOErrorException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Slf4j
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final int presignedUrlExpiration;
    private final ApplicationEventPublisher eventPublisher;

    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket,
            @Value("${discodeit.storage.s3.presigned-url-expiration:600}") int presignedUrlExpiration,
            ApplicationEventPublisher eventPublisher
    ) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;
        this.eventPublisher = eventPublisher;

        this.s3Client = getS3Client();
        this.s3Presigner = getS3Presigner();
    }

    private S3Client getS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    private S3Presigner getS3Presigner() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public UUID put(UUID id, byte[] data) {
        try {
            String key = id.toString();

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(data));

            log.info("[S3Storage] S3에 파일 업로드 성공: {}", key);
            return id;
        } catch (Exception e) {
            log.error("[S3Storage] S3 업로드 실패: {}", id, e);
            throw e;
        }
    }

    @Override
    public InputStream get(UUID id) {
        try {
            String key = id.toString();

            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            byte[] bytes = s3Client.getObjectAsBytes(getRequest).asByteArray();

            log.info("[S3Storage] S3에서 파일 조회 성공");
            return new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            log.error("[S3Storage] S3 조회 실패", e);
            throw new RuntimeException("[S3Storage] S3 조회 실패", e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDTO dto) {
        String key = dto.getId().toString();
        String presignedUrl = generatePresignedUrl(key, dto.getContentType());

        log.info("[S3Storage] Presigned URL 생성 성공: {}, presignedUrl: {}", key, presignedUrl);

        return ResponseEntity.status(302)
                .header("Location", presignedUrl)
                .build();
    }

    private String generatePresignedUrl(String key, String contentType) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                .getObjectRequest(getRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    @Recover
    public UUID recoverS3Upload(Exception e, UUID id, byte[] data) {
        String requestId = MDC.get("requestId");
        requestId = requestId == null ? "null" : requestId;

        log.error("[S3Storage] S3 파일업로드 실패 Recover 메서드 실행");

        eventPublisher.publishEvent(
                new S3UploadFailedEvent(
                        id,
                        requestId,
                        e.getMessage()
                )
        );

        throw new FileIOErrorException(e);
    }
}