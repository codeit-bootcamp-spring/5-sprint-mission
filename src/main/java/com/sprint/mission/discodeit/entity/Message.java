package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private String text;
    private UUID channelId;
    private UUID userId;
    private Long createdAt;
    private Long updatedAt;

    public Message() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    }

    public Message(String text, UUID channelId, UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.text = text;
        this.channelId = channelId;
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    private void setId(UUID id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    private void setText(String text) {
        this.text = text;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    private void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void update(String text) {
        setText(text);
        setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Message{" +
            "id=" + id +
            ", text='" + text + '\'' +
            ", channelId=" + channelId +
            ", userId=" + userId +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
