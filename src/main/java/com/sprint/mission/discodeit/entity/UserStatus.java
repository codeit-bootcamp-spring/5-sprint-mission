package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class UserStatus implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;
  private static final int TIME_DIFF = 300;

  private final UUID id;
  private final UUID userId;
  private final Instant createdAt;
  private Instant updatedAt;
  private boolean loginStatus;

  public UserStatus(UUID userId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.loginStatus = false;

    this.userId = userId;
  }

  public void update(boolean loginStatus) {
    boolean anyValueUpdated = false;

    if (this.loginStatus != loginStatus) {
      this.loginStatus = loginStatus;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      updatedAt = Instant.now();
    }
  }

  public boolean isLogin() {
    loginStatus = updatedAt != null
        && updatedAt.getEpochSecond() - Instant.now().getEpochSecond() <= TIME_DIFF;
    return loginStatus;
  }
}
