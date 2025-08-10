package com.sprint.mission.discodeit.entity;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

import java.io.Serializable;

@NoArgsConstructor(force = true)
@SuperBuilder
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    protected UUID id;
    protected Instant createdAt;
    protected Instant updatedAt;

    

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    protected void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

}
