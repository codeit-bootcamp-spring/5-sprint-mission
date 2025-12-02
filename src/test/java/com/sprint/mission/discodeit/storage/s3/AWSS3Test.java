package com.sprint.mission.discodeit.storage.s3;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayName("S3 API 테스트")
class AWSS3Test {

    private static final String TEST_CONTENT = "Hello from .env via Properties!";
    private static final String TEST_CONTENT_DOWNLOAD = "Test content for download";
    private static final String TEST_CONTENT_PRESIGNED = "Test content for presigned URL";
    private static final String TEXT_PLAIN = "text/plain";
    private static final Duration PRESIGNED_URL_DURATION = Duration.ofMinutes(10);

    private static String accessKey;
    private static String secretKey;
    private static String region;
    private static String bucket;
    private S3Client s3Client;
    private S3Presigner presigner;
    private String testKey;

    @BeforeAll
    static void loadEnv() throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
        }

        accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
        secretKey = props.getProperty("AWS_S3_SECRET_KEY");
        region = props.getProperty("AWS_S3_REGION");
        bucket = props.getProperty("AWS_S3_BUCKET");

        if (accessKey == null || secretKey == null || region == null || bucket == null) {
            throw new IllegalStateException("AWS S3 설정이 .env 파일에 올바르게 정의되지 않았습니다.");
        }
    }

    @BeforeEach
    void setUp() {
        s3Client = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .build();

        presigner = S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .build();

        testKey = "test-" + UUID.randomUUID();
    }

    private PutObjectRequest createPutObjectRequest(String key) {
        return PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(TEXT_PLAIN)
            .build();
    }

    private GetObjectRequest createGetObjectRequest(String key) {
        return GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
    }

    private void uploadTestContent(String key, String content) {
        PutObjectRequest request = createPutObjectRequest(key);
        s3Client.putObject(request, RequestBody.fromString(content));
    }

    @Test
    @DisplayName("S3에 파일을 업로드한다")
    void uploadToS3() {
        // when
        uploadTestContent(testKey, TEST_CONTENT);

        // then
        GetObjectRequest request = createGetObjectRequest(testKey);
        String downloadedContent = s3Client.getObjectAsBytes(request).asUtf8String();
        assertThat(downloadedContent).isEqualTo(TEST_CONTENT);

        log.info("파일 업로드 성공: {}", testKey);
    }

    @Test
    @DisplayName("S3에서 파일을 다운로드한다")
    void downloadFromS3() {
        // given
        uploadTestContent(testKey, TEST_CONTENT_DOWNLOAD);

        // when
        GetObjectRequest request = createGetObjectRequest(testKey);
        String downloadedContent = s3Client.getObjectAsBytes(request).asUtf8String();

        // then
        assertThat(downloadedContent).isEqualTo(TEST_CONTENT_DOWNLOAD);

        log.info("다운로드된 파일 내용: {}", downloadedContent);
    }

    @Test
    @DisplayName("S3 파일에 대한 Presigned URL을 생성한다")
    void generatePresignedUrl() {
        // given
        uploadTestContent(testKey, TEST_CONTENT_PRESIGNED);
        GetObjectRequest getObjectRequest = createGetObjectRequest(testKey);

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(PRESIGNED_URL_DURATION)
            .getObjectRequest(getObjectRequest)
            .build();

        // when
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
        URL url = presignedRequest.url();

        // then
        assertThat(url).isNotNull();
        assertThat(url.toString()).contains(bucket);
        assertThat(url.toString()).contains(testKey);

        log.info("Presigned URL 생성 완료: key={}", testKey);
    }

    @AfterEach
    void cleanup() {
        if (testKey != null && s3Client != null) {
            try {
                DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(testKey)
                    .build();
                s3Client.deleteObject(request);
                log.info("테스트 파일 정리 완료: {}", testKey);
            } catch (S3Exception e) {
                log.warn("테스트 파일 정리 실패: {}", e.getMessage());
            }
        }

        if (presigner != null) {
            presigner.close();
        }
        if (s3Client != null) {
            s3Client.close();
        }
    }
}
