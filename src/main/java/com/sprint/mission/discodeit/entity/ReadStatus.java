package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReadStatus implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private final UUID id;
  private final UUID userId;
  private final UUID channelId;
  private final Instant createdAt;
  private Instant updatedAt;
  private Instant lastReadAt;

  public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.lastReadAt = lastReadAt;

    this.userId = userId;
    this.channelId = channelId;
  }

  public void update(Instant newLastReadAt) {
    boolean anyValueUpdated = false;
    if (newLastReadAt != null && this.lastReadAt != newLastReadAt) {
      this.lastReadAt = newLastReadAt;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      updatedAt = Instant.now();
    }
  }
}
