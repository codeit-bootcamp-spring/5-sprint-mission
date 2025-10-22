package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.local.LocalBinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3StorageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(S3StorageProperties.class)
public class StorageConfig {

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "s3")
  public BinaryContentStorage s3BinaryContentStorage(S3StorageProperties props) {
    S3StorageProperties.S3 s3 = props.getS3();
    return new S3BinaryContentStorage(
        s3.getAccessKey(),
        s3.getSecretKey(),
        s3.getRegion(),
        s3.getBucket(),
        s3.getPresignedUrlExpiration()
    );
  }

  @Bean
  @ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local", matchIfMissing = true)
  public BinaryContentStorage localBinaryContentStorage(S3StorageProperties props) {
    // 이미 가지고 있는 Local 구현체로 바꿔 끼우기
    return new LocalBinaryContentStorage(props.getLocal().getRootPath());
  }
}

