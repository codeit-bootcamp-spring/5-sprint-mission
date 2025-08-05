package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected final UUID id;
    protected final Instant createAt;
    protected Instant updateAt;

    protected BaseEntity() {
        this(UUID.randomUUID(), Instant.now());
    }

    protected BaseEntity(UUID id, Instant createAt) {
        this.id = id;
        this.createAt = createAt;
    }

    public void updateTimeStamp() {
        updateAt = Instant.now();
    }
}
