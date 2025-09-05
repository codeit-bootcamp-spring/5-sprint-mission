package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import lombok.Getter;

import java.time.Instant;

@Getter
public class UserStatus extends BaseUpdatableEntity {
  private User user;                 // 1..1
  private Instant lastActiveAt;

  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;
  }

  public void update(Instant newLastActiveAt) {
    if (newLastActiveAt != null) this.lastActiveAt = newLastActiveAt;
  }
}
