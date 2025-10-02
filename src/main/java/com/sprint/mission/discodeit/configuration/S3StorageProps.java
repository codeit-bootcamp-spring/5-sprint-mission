package com.sprint.mission.discodeit.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit.storage.s3")
@Getter
@Setter
public class S3StorageProps {

  public String accessKey;
  public String secretKey;
  public String bucket;
  public String region;
  public String presignedUrlExpiration = "600";
}
