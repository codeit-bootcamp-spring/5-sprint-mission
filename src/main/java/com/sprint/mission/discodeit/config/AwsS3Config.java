package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import static org.springframework.util.StringUtils.hasText;

@Configuration
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "s3")
@RequiredArgsConstructor
public class AwsS3Config {

    private final S3Properties s3Properties;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        if (hasText(s3Properties.accessKey()) && hasText(s3Properties.secretKey())) {
            return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(s3Properties.accessKey(), s3Properties.secretKey())
            );
        }
        return DefaultCredentialsProvider.create();
    }

    @Bean
    public S3Client s3Client(AwsCredentialsProvider credentialsProvider) {
        return S3Client.builder()
            .region(Region.of(s3Properties.region()))
            .credentialsProvider(credentialsProvider)
            .build();
    }

    @Bean
    public S3Presigner s3Presigner(AwsCredentialsProvider credentialsProvider) {
        return S3Presigner.builder()
            .region(Region.of(s3Properties.region()))
            .credentialsProvider(credentialsProvider)
            .build();
    }
}
