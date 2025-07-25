package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected final UUID id;
    protected final Long createAt;
    protected Long updateAt;

    protected BaseEntity() {
        this(UUID.randomUUID(), Instant.now().getEpochSecond());
    }

    protected BaseEntity(UUID id, Long createAt) {
        this.id = id;
        this.createAt = createAt;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void updateTimeStamp() {
        updateAt = Instant.now().getEpochSecond();
    }
}
