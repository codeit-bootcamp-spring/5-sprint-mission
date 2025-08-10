package com.sprint.mission.discodeit.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private boolean deleted;

    protected BaseEntity(UUID id, Instant createdAt, Instant updatedAt) {
        this.id = (id != null) ? id : UUID.randomUUID();
        Instant now = Instant.now();
        this.createdAt = (createdAt != null) ? createdAt : now;
        this.updatedAt = (updatedAt != null) ? updatedAt : this.createdAt;
    }

    protected BaseEntity(UUID id, Instant createdAt) {
        this(id, createdAt, null);
    }

    protected BaseEntity(UUID id) {
        this(id, null, null);
    }

    protected BaseEntity() {
        this(null, null, null);
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }

    public void delete() {
        if (!this.deleted) {
            this.deleted = true;
            touch();
        }
    }

    public void restore() {
        if (this.deleted) {
            this.deleted = false;
            touch();
        }
    }
}
