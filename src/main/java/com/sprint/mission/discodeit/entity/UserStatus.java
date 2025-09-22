package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_statuses")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column
  private Instant lastActiveAt;

  public boolean isOnline() {
    return lastActiveAt.isAfter(Instant.now()
                                       .minus(Duration.ofMinutes(5)));
  }

  public void update() {
    this.lastActiveAt = Instant.now();
  }
}
