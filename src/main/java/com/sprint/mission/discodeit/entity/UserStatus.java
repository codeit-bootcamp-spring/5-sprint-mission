package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true) // 1:1 보장
  private User user;

  @Column(name = "last_active_at", nullable = false)
  private Instant lastActiveAt;

  protected UserStatus() {}

  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;
  }

  public void update(Instant newLastActiveAt) {
    this.lastActiveAt = newLastActiveAt;
  }
}
