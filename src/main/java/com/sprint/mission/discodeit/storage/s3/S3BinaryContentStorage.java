package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Component
public class S3BinaryContentStorage implements BinaryContentStorage {

    @Value("${discodeit.s3.bucket}")
    private String bucketName;

    @Value("${discodeit.s3.access-key}")
    private String accessKey;

    @Value("${discodeit.s3.secret-key}")
    private String secretKey;

    @Value("${discodeit.s3.region}")
    private String region;

    private S3Client s3Client;
    private S3Presigner s3Presigner;

    @PostConstruct
    public void init() {
        StaticCredentialsProvider credentialsProvider =
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));

        this.s3Client = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .build();

        this.s3Presigner = S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .build();
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        String key = binaryContentId.toString();
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentLength((long) bytes.length)
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
            return binaryContentId;
        } catch (S3Exception e) {
            System.err.println("S3 Put failed: " + e.getMessage());
            throw new RuntimeException("S3 Put failed for key: " + key, e);
        }
    }
    @Override
    public InputStream get(UUID binaryContentId) {
        String key = binaryContentId.toString();
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            // S3 객체를 스트림으로 반환
            return s3Client.getObject(getObjectRequest);
        } catch (NoSuchKeyException e) {
            System.err.println("S3 Get failed: Key not found: " + key);
            return null;
        } catch (S3Exception e) {
            System.err.println("S3 Get failed: " + e.getMessage());
            throw new RuntimeException("S3 Get failed for key: " + key, e);
        }
    }
    @Override
    public ResponseEntity<?> download(BinaryContentDto metaData) {
        throw new UnsupportedOperationException("Unimplemented method 'download'");
    }

    @Value("${discodeit.s3.presigned-url-expiration:600}")
    private long presignedUrlExpirationSeconds;

    @Override
    public String generatePresignedUrl(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofSeconds(presignedUrlExpirationSeconds))
                .build();

            PresignedGetObjectRequest presignedObjectRequest = s3Presigner.presignGetObject(presignRequest);

            return presignedObjectRequest.url().toString();
        } catch (Exception e) {
            System.err.println("Presigned URL generation failed for key " + key + ": " + e.getMessage());
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
}
