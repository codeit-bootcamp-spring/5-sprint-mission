package com.sprint.mission.discodeit.common.config;

import com.sprint.mission.discodeit.common.config.properties.StorageProperties;
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

    private final StorageProperties storageProperties;

    @Bean
    public Region awsRegion() {
        String region = storageProperties.s3().region();
        if (!hasText(region)) {
            throw new IllegalArgumentException("discodeit.storage.s3.region must not be empty when storage type is s3");
        }
        return Region.of(region);
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        String accessKey = storageProperties.s3().accessKey();
        String secretKey = storageProperties.s3().secretKey();

        if (!hasText(accessKey) || !hasText(secretKey)) {
            return DefaultCredentialsProvider.create();
        }

        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    @Bean(destroyMethod = "close")
    public S3Client s3Client(Region awsRegion, AwsCredentialsProvider credentialsProvider) {
        return S3Client.builder()
            .region(awsRegion)
            .credentialsProvider(credentialsProvider)
            .build();
    }

    @Bean(destroyMethod = "close")
    public S3Presigner s3Presigner(Region awsRegion, AwsCredentialsProvider credentialsProvider) {
        return S3Presigner.builder()
            .region(awsRegion)
            .credentialsProvider(credentialsProvider)
            .build();
    }
}
