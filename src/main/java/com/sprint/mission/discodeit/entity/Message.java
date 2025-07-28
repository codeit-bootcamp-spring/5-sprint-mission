package com.sprint.mission.discodeit.entity;

import java.util.Date;
import java.util.UUID;

public class Message {
    private final UUID id;
    private final Long createdAt;
    private final UUID userId;
    private final UUID channelId;
    private Long updatedAt;
    private String content;

    public Message(UUID userId, UUID channelId, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public String getContent() {
        return content;
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return String.format(
                "[메시지 UUID: %s]\n[유저 UUID: %s]\n[채널 UUID: %s]\n[내용: %s]\n[생성일: %s]\n[수정일: %s]",
                id,
                userId,
                channelId,
                content,
                new Date(createdAt),
                updatedAt == null ? "없음" : new Date(updatedAt)
        );
    }
}
