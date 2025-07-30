package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String content;
    private UUID senderId;
    private UUID channelId;

    public Message(String content, UUID senderId, UUID channelId) {
        id = UUID.randomUUID();
        createdAt = Instant.now().toEpochMilli();
        updatedAt = createdAt;
        this.senderId = senderId;
        this.channelId = channelId;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getContent() {
        return content;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void updateContent(String content) {
        this.content = content;
        updatedAt = Instant.now().toEpochMilli();
    }

    public void updateSenderId(UUID senderId) {
        this.senderId = senderId;
        updatedAt = Instant.now().toEpochMilli();
    }

    public void updateChannelId(UUID channelId) {
        this.channelId = channelId;
        updatedAt = Instant.now().toEpochMilli();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return createdAt == message.createdAt && updatedAt == message.updatedAt && Objects.equals(id, message.id) && Objects.equals(content, message.content) && Objects.equals(senderId, message.senderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt, content, senderId);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", createdAt=" + Instant.ofEpochMilli(createdAt) +
                ", updatedAt=" + Instant.ofEpochMilli(updatedAt) +
                ", content='" + content + '\'' +
                ", senderId=" + senderId +
                '}';
    }
}
