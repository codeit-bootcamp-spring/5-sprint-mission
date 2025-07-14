package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private String text;
    private UUID channelId;
    private UUID userId;

    public Message(String text, UUID channelId, UUID userId) {
        super();
        this.text = text;
        this.channelId = channelId;
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    private void setText(String text) {
        this.text = text;
    }

    public UUID getChannelId() {
        return channelId;
    }

    private void setChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public UUID getUserId() {
        return userId;
    }

    private void setUserId(UUID userId) {
        this.userId = userId;
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
