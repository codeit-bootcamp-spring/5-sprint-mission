package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private long createdAt;
    private long updatedAt;

    public Message(UUID id, long createdAt) {
        this.id = id;
        this.createdAt = createdAt;
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

    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void updateUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
