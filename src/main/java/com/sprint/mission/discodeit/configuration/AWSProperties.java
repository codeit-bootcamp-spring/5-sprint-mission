package com.sprint.mission.discodeit.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AWSProperties {

  private final Properties props = new Properties();

  public AWSProperties() {
    try (FileInputStream fis = new FileInputStream(".env")) {
      props.load(fis);
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO: 커스텀 예외 만들것
    }
  }

  public String getAccessKey() {
    return props.getProperty("AWS_S3_ACCESS_KEY");
  }

  public String getSecretKey() {
    return props.getProperty("AWS_S3_SECRET_KEY");
  }

  public String getRegion() {
    return props.getProperty("AWS_S3_REGION");
  }

  public String getBucket() {
    return props.getProperty("AWS_S3_BUCKET");
  }

}
