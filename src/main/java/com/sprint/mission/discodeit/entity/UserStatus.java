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

  /* 마지막 접속 시간을 가짐
   * 5분 이내 접속 여부 기준 메서드 가짐
   * */


  /* 유저상태와 유저 1:1
   * 하나의 UserStatus는 하나의 User를 가진다
   * FK주인이다.
   */
  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // 마지막 접속시간
  private Instant lastActiveAt;


  public UserStatus(Instant lastActiveAt) {
    this.lastActiveAt = lastActiveAt;
  }

  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;
  }

  public void setLastActiveAt(Instant newLastActiveAt) {
    this.lastActiveAt = newLastActiveAt;
  }

  // 5분 이내 접속이면 온라인
  public boolean isOnline() {
    return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
  }
}


