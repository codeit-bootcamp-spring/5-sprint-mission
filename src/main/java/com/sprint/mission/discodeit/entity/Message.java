package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Message {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String content;
    private String senderId;

    public Message(String content, String senderId) {
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

    public String getSender() {
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

    public boolean updateSender(String senderId) {
        if (this.senderId == senderId) {
            return false;
        }
        this.senderId = senderId;
        updatedAt = Instant.now().getEpochSecond();
        return true;
    }
}
