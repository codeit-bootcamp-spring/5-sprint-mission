package com.sprint.mission.discodeit.security.scheduler;

import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtCleanupScheduler {

  private final JwtRegistry jwtRegistry;

  @Scheduled(fixedDelay = 1000 * 60 * 5) // 5분
  public void clearExpiredTokens() {
    jwtRegistry.clearExpiredJwtInformation();
    log.info("Expired JWTs cleared");
  }
}
