package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public abstract class BaseEntity {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private boolean deleted;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    protected BaseEntity(UUID id, long createdAt, long updatedAt) {
        this.id = id != null ? id : UUID.randomUUID();
        this.createdAt = createdAt > 0 ? createdAt : System.currentTimeMillis();
        this.updatedAt = updatedAt > 0 ? updatedAt : this.createdAt;
    }

    protected BaseEntity(UUID id, long createdAt) {
        this(id, createdAt, createdAt);
    }

    protected BaseEntity() {
        this(UUID.randomUUID(), System.currentTimeMillis());
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

    public void touch() {
        this.updatedAt = System.currentTimeMillis();
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void delete() {
        this.deleted = true;
        touch();
    }

    public void restore() {
        this.deleted = false;
        touch();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseEntity that = (BaseEntity) o;
        if (id == null || that.id == null) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdAtStr = FORMATTER.format(Instant.ofEpochMilli(createdAt));
        String updatedAtStr = FORMATTER.format(Instant.ofEpochMilli(updatedAt));
        return "BaseEntity{"
                + "id="
                + id
                + ", createdAt="
                + createdAtStr
                + ", updatedAt="
                + updatedAtStr
                + ", deleted="
                + deleted
                + '}';
    }
}
