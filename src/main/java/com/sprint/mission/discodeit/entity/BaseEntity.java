package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class BaseEntity {
    protected UUID id;
    protected Long createdAt;
    protected Long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
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

    protected void touch() {
        this.updatedAt = System.currentTimeMillis();
    }
}
