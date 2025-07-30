package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private UUID userId;
    private UUID channelId;
    private String content;

    public Message(UUID userId, UUID channelId, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
        this.updatedAt = this.createdAt;
    }

    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public UUID getUserId() { return userId; }
    public UUID getChannelId() { return channelId; }
    public String getContent() { return content; }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}
