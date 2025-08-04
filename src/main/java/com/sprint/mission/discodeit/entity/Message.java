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

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void update(String text) {
        this.text = text;
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
