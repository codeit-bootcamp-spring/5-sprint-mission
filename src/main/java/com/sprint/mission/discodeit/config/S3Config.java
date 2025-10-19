package com.sprint.mission.discodeit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    // ✅ 기존 discodeit.storage.s3.* 키를 그대로 사용
    @Value("${discodeit.storage.s3.access-key}")
    private String accessKeyId;

    @Value("${discodeit.storage.s3.secret-key}")
    private String secretKey;

    @Value("${discodeit.storage.s3.region}")
    private String region;

    @Value("${discodeit.storage.s3.bucket}")
    private String bucket;

    @Value("${discodeit.storage.s3.presigned-url-expiration:600}")
    private long presignedExpirationSeconds;

    @Bean
    public S3Client s3Client() {
        // 자격증명 객체 생성
        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKeyId, secretKey);

        // S3 동기 클라이언트 생성
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKeyId, secretKey);
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .build();
    }

    // 서비스에서 버킷/만료 값을 편히 쓰도록 전달용 DTO
    @Bean
    public S3Props s3Props() {
        return new S3Props(bucket, presignedExpirationSeconds);
    }

    public record S3Props(String bucket, long presignedExpirationSeconds) {}
}
