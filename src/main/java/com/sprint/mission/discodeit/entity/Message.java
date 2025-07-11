package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {

    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private final Long createdAt;
    private Long updatedAt;
    private String content;

    public Message(UUID userId, UUID channelId, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getChannelId() { return channelId; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getContent() { return content; }

    public void update(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", userId=" + userId +
                ", channelId=" + channelId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", content='" + content + '\'' +
                '}';
    }
}
