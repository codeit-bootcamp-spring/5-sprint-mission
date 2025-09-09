package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

//  private static final long serialVersionUID = 1L;
  //
  @JoinColumn(unique = true, nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @OneToOne
  @JsonBackReference
  private User user;

  private Instant lastActiveAt;

//  public UserStatus(UUID userId, Instant lastActiveAt) {
//
//    this.userId = userId;
//    this.lastActiveAt = lastActiveAt;
//  }
  public UserStatus(User user) {
    this.user = user;
  }


  public UserStatus(User user, Instant lastActiveAt) {

    this.user = user;
    this.lastActiveAt = lastActiveAt;
  }

  public UserStatus() {

  }

  public void update(Instant lastActiveAt) {
    boolean anyValueUpdated = false;
    if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
      this.lastActiveAt = lastActiveAt;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }

  public Boolean isOnline() {
    Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

    return lastActiveAt.isAfter(instantFiveMinutesAgo);
  }
}
