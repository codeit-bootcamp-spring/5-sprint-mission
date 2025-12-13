package com.sprint.mission.discodeit.infrastructure.storage;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStorage;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentStorageException;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
import com.sprint.mission.discodeit.global.config.properties.S3Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Component
@Slf4j
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String bucket;
    private final Duration presignedUrlExpiration;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3BinaryContentStorage(
        S3Client s3Client,
        S3Presigner s3Presigner,
        S3Properties s3Properties
    ) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucket = s3Properties.bucket();
        this.presignedUrlExpiration = s3Properties.presignedUrlExpiration();
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
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
        } catch (S3Exception e) {
            log.error("S3 스토리지 파일 저장 실패: key={}", key, e);

            throw new BinaryContentStorageException(e);
        }
    }

    @Override
    public ResponseEntity<Void> download(BinaryContentDto metaData) {
        String key = metaData.id().toString();
        String presignedUrl = generatePresignedUrl(key, metaData.fileName(), metaData.contentType());

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, presignedUrl)
            .build();
    }

    private String generatePresignedUrl(String key, String fileName, String contentType) {
        log.debug("S3 Presigned URL 생성 시도: key={}", key);

        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");
            String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .responseContentType(contentType)
                .responseContentDisposition(contentDisposition)
                .bucket(bucket)
                .key(key)
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(presignedUrlExpiration)
                .getObjectRequest(getObjectRequest)
                .build();

            String url = s3Presigner.presignGetObject(presignRequest).url().toString();

            log.info("S3 Presigned URL 생성 완료: key={}", key);

            return url;
        } catch (S3Exception e) {
            log.error("S3 Presigned URL 생성 실패: key={}", key, e);

            throw new BinaryContentStorageException(e);
        }
    }
}
