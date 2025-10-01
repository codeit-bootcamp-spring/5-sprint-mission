package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.storage.S3.AWSProperties;
import com.sprint.mission.discodeit.storage.S3.S3ClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.beans.ConstructorProperties;

@Configuration
public class S3Config {

    private final AWSProperties props = new AWSProperties();

    @Bean
    public S3Client s3Client() {
        return S3ClientFactory.create(props);
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())
                        )
                )
                .build();
    }
}
