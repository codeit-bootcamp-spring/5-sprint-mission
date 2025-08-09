package com.sprint.mission.discodeit.domain.entitydev;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
public abstract class DevBaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private boolean deleted;

    protected DevBaseEntity(UUID id, Instant createdAt, Instant updatedAt) {
        this.id = (id != null) ? id : UUID.randomUUID();
        Instant now = Instant.now();
        this.createdAt = (createdAt != null) ? createdAt : now;
        this.updatedAt = (updatedAt != null) ? updatedAt : this.createdAt;
    }

    protected DevBaseEntity(UUID id, Instant createdAt) {
        this(id, createdAt, null);
    }

    protected DevBaseEntity(UUID id) {
        this(id, null, null);
    }

    protected DevBaseEntity() {
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
