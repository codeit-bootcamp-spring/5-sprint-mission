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
    private Instant deletedAt;
    private Instant purgeAt;

    private long version;

    protected BaseEntity(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            boolean deleted,
            Instant deletedAt,
            Instant purgeAt,
            long version
    ) {
        Instant now = Instant.now();
        this.id = (id != null) ? id : UUID.randomUUID();

        this.createdAt = (createdAt != null) ? createdAt : now;
        this.updatedAt = (updatedAt != null) ? updatedAt : this.createdAt;

        this.deleted = deleted;
        this.deletedAt = deleted ? (deletedAt != null ? deletedAt : now) : null;
        this.purgeAt = purgeAt;

        this.version = Math.max(0L, version);
    }

    protected BaseEntity(UUID id, Instant createdAt, Instant updatedAt) {
        this(id, createdAt, updatedAt, false, null, null, 0L);
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

    protected void touch() {
        this.updatedAt = Instant.now();
        this.version++;
    }

    public void delete() {
        if (!this.deleted) {
            this.deleted = true;
            this.deletedAt = Instant.now();
            this.purgeAt = null;
            touch();
        }
    }

    public void delete(Instant purgeAt) {
        if (!this.deleted) {
            this.deleted = true;
            this.deletedAt = Instant.now();
        }
        this.purgeAt = purgeAt;
        touch();
    }

    public void restore() {
        if (this.deleted || this.deletedAt != null || this.purgeAt != null) {
            this.deleted = false;
            this.deletedAt = null;
            this.purgeAt = null;
            touch();
        }
    }

    public void schedulePurge(Instant when) {
        if (when == null) {
            throw new IllegalArgumentException("purgeAt 는 null 일 수 없습니다.");
        }
        this.purgeAt = when;
        touch();
    }

    public void cancelPurge() {
        if (this.purgeAt != null) {
            this.purgeAt = null;
            touch();
        }
    }

    public boolean shouldPurge() {
        return shouldPurge(Instant.now());
    }

    public boolean shouldPurge(Instant now) {
        return this.deleted && this.purgeAt != null && !this.purgeAt.isAfter(now);
    }

    public void requireVersion(long expected) {
        if (this.version != expected) {
            throw new IllegalStateException("버전 불일치: expected=" + expected + ", actual=" + this.version);
        }
    }
}
