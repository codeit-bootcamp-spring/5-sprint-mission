package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public abstract class BaseEntity {
    protected final UUID id;
    protected final long createdAt;
    protected long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
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

    public void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }
}
