package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "read_statuses")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "channel_id")
  private Channel channel;

  @Column
  private Instant lastReadAt;

  public void update() {
    this.lastReadAt = Instant.now();
  }
}