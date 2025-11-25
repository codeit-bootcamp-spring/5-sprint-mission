package com.sprint.mission.discodeit.security.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class JwtProperties {

  @Value("${jwt.access-secret}")
  private String accessSecret;

  @Value("${jwt.refresh-secret}")
  private String refreshSecret;

  private final long accessTokenValidityMillis = 3600000L;

  private final long refreshTokenValidityMillis = 21600000L;
}