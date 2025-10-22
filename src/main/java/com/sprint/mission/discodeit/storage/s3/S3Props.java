package com.sprint.mission.discodeit.storage.s3;

import lombok.Getter; import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "discodeit.storage.s3")
public class S3Props {
  private String accessKey;
  private String secretKey;
  private String region = "ap-northeast-2";
  private String bucket;
  private int presignedUrlExpiration = 600; // seconds
}
