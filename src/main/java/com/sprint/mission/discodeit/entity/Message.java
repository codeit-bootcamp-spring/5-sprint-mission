package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Message extends BaseEntity {
    private String content;
    private UUID channelId;
    private UUID authorId;

    public Message(UUID channelId, UUID authorId, String content) {
        super(UUID.randomUUID(), Instant.now().getEpochSecond(), Instant.now().getEpochSecond());
        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
    }

    public void update(String content) {
        boolean anyValueUpdated = false;
        if (content != null && !content.equals(this.content)) {
            this.content = content;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now().getEpochSecond();
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
