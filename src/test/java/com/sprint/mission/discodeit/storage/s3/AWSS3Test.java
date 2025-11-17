package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class AWSS3Test {

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private String bucket;
    private String testKey = "test-file.txt";

    private static S3Client staticS3Client;
    private static String staticBucket;
    private static String staticTestKey;

    @BeforeEach
    void setup() throws IOException {
        // 1. 환경 변수에서 먼저 읽기 (GitHub Actions용)
        String accessKey = System.getenv("AWS_S3_ACCESS_KEY");
        String secretKey = System.getenv("AWS_S3_SECRET_KEY");
        String region = System.getenv("AWS_S3_REGION");
        bucket = System.getenv("AWS_S3_BUCKET");

        // 2. 환경 변수가 없으면 .env 파일에서 읽기 (로컬용)
        if (accessKey == null || accessKey.isEmpty()) {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(".env"));
                accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
                secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
                region = properties.getProperty("AWS_S3_REGION");
                bucket = properties.getProperty("AWS_S3_BUCKET");
            } catch (IOException e) {
                // .env 파일도 없으면 테스트 스킵
                org.junit.jupiter.api.Assumptions.assumeTrue(false,
                        "AWS credentials not available - skipping S3 tests");
                return;
            }
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        staticS3Client = s3Client;
        staticBucket = bucket;
        staticTestKey = testKey;
    }

    @Test
    void uploadTest() throws IOException {
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "Hello AWS S3!");

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(testKey)
                .contentType("text/plain")
                .build();

        s3Client.putObject(putRequest, RequestBody.fromFile(tempFile));

        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(testKey)
                .build();

        HeadObjectResponse headResponse = s3Client.headObject(headRequest);
        assertThat(headResponse.contentLength()).isGreaterThan(0);

        Files.delete(tempFile);
    }

    @Test
    void downloadTest() throws IOException {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(testKey)
                .build();

        Path downloadPath = Files.createTempFile("s3-download-", ".txt");

        Files.deleteIfExists(downloadPath);

        s3Client.getObject(getRequest, downloadPath);

        String content = Files.readString(downloadPath);
        assertThat(content).contains("Hello AWS S3!");

        Files.delete(downloadPath);
    }

    @Test
    void presignedUrlTest() {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(testKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        String presignedUrl = presignedRequest.url().toString();

        assertThat(presignedUrl).isNotEmpty();
        assertThat(presignedUrl).contains(bucket);
        assertThat(presignedUrl).contains(testKey);
    }

    @AfterAll
    static void cleanup() {
        if (staticS3Client != null && staticBucket != null) {
            try {
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(staticBucket)
                        .key(staticTestKey)
                        .build();

                staticS3Client.deleteObject(deleteRequest);
                System.out.println("S3 test file deleted: " + staticTestKey);
            } catch (Exception e) {
                System.err.println("Failed to delete S3 test file: " + e.getMessage());
            }
        }
    }
}