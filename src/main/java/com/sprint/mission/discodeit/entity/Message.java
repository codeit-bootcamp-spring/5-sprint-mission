package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Message implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private final UUID id;
  private final List<UUID> attachmentIds;
  private final Instant createdAt;
  private Instant updatedAt;
  //
  private String content;
  //
  private final UUID channelId;
  private final UUID authorId;

  public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.attachmentIds = List.copyOf(attachmentIds);
    //
    this.content = content;
    this.channelId = channelId;
    this.authorId = authorId;
  }

  public void update(String content) {
    boolean anyValueUpdated = false;
    if (content != null && !content.equals(this.content)) {
      this.content = content;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }
}
