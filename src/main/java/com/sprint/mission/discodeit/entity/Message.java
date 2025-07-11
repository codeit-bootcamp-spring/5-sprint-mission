package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Message {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String content;
    private User sender;
    private User receiver;

    public Message(User sender, User receiver, String content) {
        id = UUID.randomUUID();
        createdAt = Instant.now().getEpochSecond();
        updatedAt = createdAt;
        this.sender = sender;
        this.receiver = receiver;
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
}
