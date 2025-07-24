package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
  private String content;
  private UUID channelId;
  private UUID authorId;

  public Message(UUID channelId, UUID authorId, String content) {
    super(UUID.randomUUID(), System.currentTimeMillis(), System.currentTimeMillis());
    this.channelId = channelId;
    this.authorId = authorId;
    this.content = content;
  }

  public void update(String content) {
    if (content != null) {
      this.content = content;
    }
    this.updatedAt = System.currentTimeMillis();
  }

  public UUID getMessageId() {
    return this.id;
  }

  public Long getCreatedAt() {
    return this.createdAt;
  }

  public String getContent() {
    return content;
  }

  public UUID getChannelId() {
    return channelId;
  }

  public UUID getAuthorId() {
    return authorId;
  }

  @Override
  public String toString() {
    return "Message{" +
        "id=" + id +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", channelId=" + channelId +
        ", authorId=" + authorId +
        ", content='" + content + "'" +
        '}';
  }
}
