package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Message {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String content;
    private UUID senderId;

    public Message(String content, UUID senderId) {
        id = UUID.randomUUID();
        createdAt = Instant.now().getEpochSecond();
        updatedAt = createdAt;
        this.senderId = senderId;
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

    public UUID getSender() {
        return senderId;
    }

    public boolean updateContent(String content) {
        if (this.content == content) {
            return false;
        }
        this.content = content;
        updatedAt = Instant.now().getEpochSecond();
        return true;
    }

    public boolean updateSender(UUID senderId) {
        if (this.senderId == senderId) {
            return false;
        }
        this.senderId = senderId;
        updatedAt = Instant.now().getEpochSecond();
        return true;
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
                ", createdAt=" + Instant.ofEpochSecond(createdAt) +
                ", updatedAt=" + Instant.ofEpochSecond(updatedAt) +
                ", content='" + content + '\'' +
                ", senderId=" + senderId +
                '}';
    }
}
