package com.sprint.mission.discodeit.domain.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ReadStatus extends AbstractEntity {

  private final UUID userId;
  private final UUID channelId;

  private Instant lastReadAt;

  public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
    this.userId = Objects.requireNonNull(userId, "userId must not be null");
    this.channelId = Objects.requireNonNull(channelId, "channelId must not be null");
    this.lastReadAt = Objects.requireNonNull(lastReadAt, "lastReadAt must not be null");
  }

  public void setLastReadAt(Instant lastReadAt) {
    Objects.requireNonNull(lastReadAt, "lastReadAt must not be null");
    this.lastReadAt = lastReadAt;
  }

  @Override
  public String toString() {
    return "ReadStatus[userId=%s, channelId=%s, lastReadAt=%s]"
        .formatted(userId, channelId, lastReadAt);
  }
}
