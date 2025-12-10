package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionManager {

  private final JwtRegistry jwtRegistry;

  public void expireSessionsForUser(UUID userId) {
    jwtRegistry.invalidateJwtInformationByUserId(userId);
  }

  public boolean isUserOnline(UUID userId) {
    return jwtRegistry.findByUserId(userId)
                      .isPresent();
  }
}
