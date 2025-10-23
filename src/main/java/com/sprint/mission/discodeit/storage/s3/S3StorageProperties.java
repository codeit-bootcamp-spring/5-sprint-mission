package com.sprint.mission.discodeit.storage.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "discodeit.storage")
public class S3StorageProperties {

  private String type = "local";

  @Getter @Setter
  public static class Local {
    private String rootPath = ".discodeit/storage";
  }
  private Local local = new Local();

  @Getter @Setter
  public static class S3 {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;
    private long presignedUrlExpiration = 600;
  }
  private S3 s3 = new S3();
}
