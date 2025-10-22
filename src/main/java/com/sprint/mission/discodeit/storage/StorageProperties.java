package com.sprint.mission.discodeit.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit.storage")
public class StorageProperties {
  private String type = "local";
  private Local local = new Local();
  private S3 s3 = new S3();

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public Local getLocal() { return local; }
  public S3 getS3() { return s3; }

  public static class Local {
    private String rootPath = ".discodeit/storage";
    public String getRootPath() { return rootPath; }
    public void setRootPath(String rootPath) { this.rootPath = rootPath; }
  }
  public static class S3 {
    private String accessKey;
    private String secretKey;
    private String region = "ap-northeast-2";
    private String bucket;
    private int presignedUrlExpiration = 600;

    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }
    public int getPresignedUrlExpiration() { return presignedUrlExpiration; }
    public void setPresignedUrlExpiration(int presignedUrlExpiration) {
      this.presignedUrlExpiration = presignedUrlExpiration;
    }
  }
}
