package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.properties.S3Properties;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "s3")
@Slf4j
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String bucket;
    private final Duration presignedUrlExpiration;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3BinaryContentStorage(S3Client s3Client, S3Presigner s3Presigner, S3Properties props) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;

        this.bucket = props.bucket();
        this.presignedUrlExpiration = props.presignedUrlExpiration();
    }

    @Override
    public UUID put(
        UUID binaryContentId,
        byte[] bytes
    ) {
        String key = binaryContentId.toString();

        log.debug("S3 스토리지 파일 저장 시도: key={}, size={}", key, bytes.length);

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

            s3Client.putObject(request, RequestBody.fromBytes(bytes));

            log.info("S3 스토리지 파일 저장 완료: key={}", key);

            return binaryContentId;
        } catch (Exception e) {
            log.error("S3 스토리지 파일 저장 실패: key={}", key, e);

            throw new RuntimeException("파일 저장 실패: " + binaryContentId, e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        String key = binaryContentId.toString();

        log.debug("S3 스토리지 파일 조회 시도: key={}", key);

        try {
            GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

            log.info("S3 스토리지 파일 조회 완료: key={}", key);

            byte[] bytes = s3Client.getObjectAsBytes(request).asByteArray();

            return new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            log.error("S3 스토리지 파일 조회 실패: key={}", key, e);

            throw new BinaryContentNotFoundException(binaryContentId);
        }
    }

    @Override
    public ResponseEntity<Void> download(BinaryContentDto metaData) {
        try {
            String key = metaData.id().toString();
            String presignedUrl = generatePresignedUrl(key, metaData.contentType());

            log.info("생성된 Presigned URL: {}", presignedUrl);

            return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, presignedUrl)
                .build();
        } catch (Exception e) {
            log.error("Presigned URL 생성 실패: {}", e.getMessage());

            throw new RuntimeException("Presigned URL 생성 실패", e);
        }
    }

    private String generatePresignedUrl(String key, String contentType) {
        log.debug("S3 Presigned URL 생성 시도: key={}", key);

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .responseContentType(contentType)
                .bucket(bucket)
                .key(key)
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(presignedUrlExpiration)
                .getObjectRequest(getObjectRequest)
                .build();

            String url = s3Presigner.presignGetObject(presignRequest).url().toString();

            log.info("S3 Presigned URL 생성 완료: key={}, url={}", key, url);

            return url;
        } catch (Exception e) {
            log.error("S3 Presigned URL 생성 실패: key={}", key, e);

            throw new RuntimeException("Presigned URL 생성 실패: " + key, e);
        }
    }
}
