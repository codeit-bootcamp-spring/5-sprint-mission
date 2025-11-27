package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.properties.S3Properties;
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
public class AwsS3Config {

    private final String accessKey;
    private final String secretKey;
    private final String region;

    public AwsS3Config(S3Properties props) {
        if (!props.isConfigured()) {
            throw new IllegalStateException(
                "S3 storage is enabled but not fully configured. "
                    + "Please set accessKey, secretKey, region, and bucket.");
        }
        this.accessKey = props.accessKey();
        this.secretKey = props.secretKey();
        this.region = props.region();
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        if (hasText(accessKey) && hasText(secretKey)) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
            return StaticCredentialsProvider.create(credentials);
        }
        return DefaultCredentialsProvider.create();
    }

    @Bean
    public S3Client s3Client(AwsCredentialsProvider credentialsProvider) {
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .build();
    }

    @Bean
    public S3Presigner s3Presigner(AwsCredentialsProvider credentialsProvider) {
        return S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .build();
    }
}
