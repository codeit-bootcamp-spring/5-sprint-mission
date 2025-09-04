package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "userstatuses")
@Getter
@NoArgsConstructor
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private Instant lastActiveAt; // 마지막 접속시간


  public UserStatus(Instant lastActiveAt) {
    this.lastActiveAt = lastActiveAt;
  }

  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;
  }

  public boolean isOnline() {
    // 5분 이내 접속이면 온라인
    return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
  }
}


