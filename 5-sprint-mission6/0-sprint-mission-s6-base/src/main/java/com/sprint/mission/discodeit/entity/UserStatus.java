package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_statuses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity {

  private static final Duration ONLINE_WINDOW = Duration.ofMinutes(5);

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(name = "last_active_at", nullable = false)
  private Instant lastActiveAt;

  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;
    if (user.getStatus() != this) user.setStatus(this);
  }

  void linkUser(User user) { this.user = user; }

  public boolean update(Instant newLastActiveAt) {
    if (newLastActiveAt != null && !newLastActiveAt.equals(this.lastActiveAt)) {
      this.lastActiveAt = newLastActiveAt; return true;
    }
    return false;
  }

  public boolean isOnline() {
    if (lastActiveAt == null) return false;
    return lastActiveAt.isAfter(Instant.now().minus(ONLINE_WINDOW));
  }
}