package com.sprint.mission.discodeit.global.config;

import com.sprint.mission.discodeit.global.config.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

import static org.springframework.util.StringUtils.hasText;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    @Bean
    public Region awsRegion() {
        Assert.hasText(s3Properties.region(), "discodeit.s3.region must not be empty");
        return Region.of(s3Properties.region());
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        String accessKey = s3Properties.accessKey();
        String secretKey = s3Properties.secretKey();

        if (!hasText(accessKey) || !hasText(secretKey)) {
            throw new IllegalStateException("AWS S3 accessKey or secretKey is not set in discodeit.s3 properties");
        }

        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        );
    }

    @Bean
    public S3Client s3Client(Region awsRegion, AwsCredentialsProvider credentialsProvider) {
        S3ClientBuilder builder = S3Client.builder()
            .region(awsRegion)
            .credentialsProvider(credentialsProvider);

        if (hasText(s3Properties.endpoint())) {
            builder.endpointOverride(URI.create(s3Properties.endpoint()))
                .forcePathStyle(true);
        }

        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner(Region awsRegion, AwsCredentialsProvider credentialsProvider) {
        S3Presigner.Builder builder = S3Presigner.builder()
            .region(awsRegion)
            .credentialsProvider(credentialsProvider);

        if (hasText(s3Properties.endpoint())) {
            builder.endpointOverride(URI.create(s3Properties.endpoint()));
        }

        return builder.build();
    }
}
