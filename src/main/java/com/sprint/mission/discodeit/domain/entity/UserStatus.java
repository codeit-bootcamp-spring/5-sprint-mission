package com.sprint.mission.discodeit.domain.entity;

import static java.time.Instant.now;

import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserStatus extends AbstractEntity {

  private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

  private final UUID userId;
  private UserStatusType type = UserStatusType.OFFLINE;
  private Instant lastActiveAt;
  private boolean manual;
  private boolean loggedIn;

  public UserStatus(UUID userId) {
    this.userId = Objects.requireNonNull(userId, "userId must not be null");
  }

  public UserStatusType getType() {
    if (!loggedIn) {
      return UserStatusType.OFFLINE;
    }
    if (manual) {
      return type;
    }
    Instant la = lastActiveAt;
    Instant cutoff = now().minus(ONLINE_THRESHOLD);
    return (la != null && la.isAfter(cutoff)) ? UserStatusType.ONLINE : UserStatusType.IDLE;
  }

  public void setType(UserStatusType newType) {
    Objects.requireNonNull(newType, "type must not be null");
    boolean changed = false;
    if (this.type != newType) {
      this.type = newType;
      changed = true;
    }
    boolean newManual = newType != UserStatusType.ONLINE;
    if (this.manual != newManual) {
      this.manual = newManual;
      changed = true;
    }
    if (changed) {
      touch();
    }
  }

  public void setLastActiveAt(Instant newLastActiveAt) {
    Objects.requireNonNull(newLastActiveAt, "lastActiveAt must not be null");
    if (this.lastActiveAt == null || !this.lastActiveAt.equals(newLastActiveAt)) {
      this.lastActiveAt = newLastActiveAt;
      touch();
    }
  }

  public UserStatus login() {
    if (!this.loggedIn) {
      this.loggedIn = true;
      this.manual = false;
      this.type = UserStatusType.ONLINE;
      heartbeat();
      touch();
    }
    return this;
  }

  public UserStatus logout() {
    if (this.loggedIn || this.type != UserStatusType.OFFLINE || this.lastActiveAt != null
        || this.manual) {
      this.loggedIn = false;
      this.type = UserStatusType.OFFLINE;
      this.manual = false;
      this.lastActiveAt = null;
      touch();
    }
    return this;
  }

  public UserStatus heartbeat() {
    this.lastActiveAt = now();
    return this;
  }
}
