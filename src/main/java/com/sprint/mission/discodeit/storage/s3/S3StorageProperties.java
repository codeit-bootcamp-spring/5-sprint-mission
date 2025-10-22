package com.sprint.mission.discodeit.storage.s3;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/* S3 관련 설정값 파일
 * 키,시크릿,버킷,리전,presigned 유효시간 등
 */

@Data
@Component
@ConfigurationProperties(prefix = "discodeit.storage.s3")
public class S3StorageProperties {

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucket;
  private Long presignedUrlExpiration; //초단위

  
}