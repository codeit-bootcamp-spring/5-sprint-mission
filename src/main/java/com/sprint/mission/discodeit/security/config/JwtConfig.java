package com.sprint.mission.discodeit.security.config;

import com.sprint.mission.discodeit.security.jwt.InMemoryJwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class JwtConfig {

  private final JwtProperties jwtProperties;

  @Bean
  public JwtTokenProvider jwtTokenProvider() {
    return new JwtTokenProvider(
        jwtProperties.getAccessSecret(),
        jwtProperties.getRefreshSecret(),
        jwtProperties.getAccessTokenValidityMillis(),
        jwtProperties.getRefreshTokenValidityMillis()
    );
  }

  @Bean
  public JwtRegistry jwtRegistry() {
    return new InMemoryJwtRegistry();
  }
}
