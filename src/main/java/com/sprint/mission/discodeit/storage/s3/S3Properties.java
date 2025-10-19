package com.sprint.mission.discodeit.storage.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@Getter
@Setter
public class S3Properties {

  @Value("${discodeit.storage.s3.access-key}")
  private String accessKey;

  @Value("${discodeit.storage.s3.secret-key}")
  private String secretKey;

  @Value("${discodeit.storage.s3.bucket}")
  private String bucket;

  @Value("${discodeit.storage.s3.region}")
  private String region;

  @Value("${discodeit.storage.s3.presigned-url-expiration}")
  private long expiration = 600;

  @Bean
  public S3Client s3Client() {
    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }

  @Bean
  public S3Presigner presigner() {
    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }
}


