package com.sprint.mission.discodeit.storage.s3;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/* discodeit.storage.type: s3 일 때만
 * S3BinaryContentStorage 객체를 스프링 컨테이너에 등록
 * */

@Configuration
//S3StorageProperties 클래스를 yml 속성파일과 자동매핑해주는 어노테이션
@EnableConfigurationProperties(S3StorageProperties.class)

public class S3StorageConfig {


  @Bean
  @ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
  public BinaryContentStorage s3BinaryContentStorage(S3StorageProperties props) {
    return new S3BinaryContentStorage(props);
  }
}