package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.S3BinaryContentStorage;
import com.sprint.mission.discodeit.storage.StorageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {



  @Bean
  @ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "s3")
  public BinaryContentStorage s3BinaryContentStorage(StorageProperties props) {
    var s3 = props.getS3();
    return new S3BinaryContentStorage(
        s3.getAccessKey(), s3.getSecretKey(), s3.getRegion(),
        s3.getBucket(), s3.getPresignedUrlExpiration()
    );
  }
}