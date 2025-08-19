package com.sprint.mission.discodeit.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserStatus extends BaseEntity {

  private final UUID userId;
  private Instant lastLogin;

  public UserStatus(UUID userId, Instant lastSeenAt) {
    super();
    this.userId = userId;
    this.lastLogin = lastSeenAt;
  }

  public boolean isOnline() {
    return lastLogin.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
  }

  public static UserStatus of(UUID userId) {
    return new UserStatus(userId, Instant.now());
  }

  public void update() {
    this.lastLogin = Instant.now();
    updateTimestamp();
  }
}
