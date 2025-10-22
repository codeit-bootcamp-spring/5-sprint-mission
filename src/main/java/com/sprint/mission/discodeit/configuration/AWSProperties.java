package com.sprint.mission.discodeit.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit.storage.s3")
@Getter
@Setter
public class AWSProperties {

  private String accessKey;
  private String secretKey;
  private String bucket;
  private String region;
  private long expiration = 600;
}
