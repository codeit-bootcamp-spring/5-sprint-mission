package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class BaseEntity {
    protected UUID id;
    protected Long createdAt;
    protected Long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    protected void setId(UUID id) {
        this.id = id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    protected void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    protected void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
