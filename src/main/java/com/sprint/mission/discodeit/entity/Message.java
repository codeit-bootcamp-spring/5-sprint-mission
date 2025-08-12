package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID senderId;
    private final UUID channelId;
    private final String content;
    private final long createdAt;
    private final long updatedAt;

    public Message(UUID senderId, UUID channelId, String content, long createdAt) {
        this(UUID.randomUUID(), senderId, channelId, content, createdAt, createdAt);
    }

    public Message(UUID id, UUID senderId, UUID channelId, String content, long createdAt, long updatedAt) {
        this.id = id;
        this.senderId = senderId;
        this.channelId = channelId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public String getContent() {
        return content;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public Message withContent(String newContent) {
        return new Message(id, senderId, channelId, newContent, createdAt, System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", channelId=" + channelId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}


