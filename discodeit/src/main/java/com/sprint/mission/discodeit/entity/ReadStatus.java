package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "read_statuses")
public class ReadStatus extends BaseUpdatableEntity {

//  private static final long serialVersionUID = 1L;
  //
//  private UUID userId;
//  private UUID channelId;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Channel channel;

  private Instant lastReadAt;




//  public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
//    this.userId = userId;
//    this.channelId = channelId;
//    this.lastReadAt = lastReadAt;
//  }
    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
      this.user = user;
      this.channel = channel;
      this.lastReadAt = lastReadAt;
    }



  public ReadStatus() {

  }


  public void update(Instant newLastReadAt) {
    boolean anyValueUpdated = false;
    if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = newLastReadAt;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }
}
