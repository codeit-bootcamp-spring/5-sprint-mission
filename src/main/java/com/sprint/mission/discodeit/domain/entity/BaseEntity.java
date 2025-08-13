package com.sprint.mission.discodeit.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
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
        final Instant now = now();

        this.id = (id != null) ? id : UUID.randomUUID();

        Instant created = (createdAt != null) ? createdAt : now;
        Instant updated = (updatedAt != null) ? updatedAt : created;
        if (updated.isBefore(created)) {
            throw new IllegalArgumentException("updatedAt은 createdAt 이전일 수 없습니다.");
        }
        this.createdAt = created;
        this.updatedAt = updated;

        this.deleted = deleted;
        if (!deleted) {
            this.deletedAt = null;
            this.purgeAt = null;
        } else {
            this.deletedAt = (deletedAt != null) ? deletedAt : now;
            this.purgeAt = purgeAt;
            if (this.purgeAt != null && this.purgeAt.isBefore(this.deletedAt)) {
                throw new IllegalArgumentException("purgeAt은 deletedAt 이전일 수 없습니다.");
            }
        }

        this.version = Math.max(0L, version);
        assertInvariants();
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

    protected Instant now() {
        return Instant.now();
    }

    protected void touch() {
        this.updatedAt = now();
        this.version++;
    }

    protected void touchIf(boolean condition) {
        if (condition) touch();
    }

    protected void assertInvariants() {
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalStateException("불변식 위반: updatedAt < createdAt");
        }
        if (!deleted) {
            if (deletedAt != null || purgeAt != null) {
                throw new IllegalStateException("불변식 위반: 삭제되지 않은 엔티티는 deletedAt/purgeAt이 null이어야 합니다.");
            }
        } else {
            if (deletedAt == null) {
                throw new IllegalStateException("불변식 위반: 삭제된 엔티티는 deletedAt이 null일 수 없습니다.");
            }
            if (purgeAt != null && purgeAt.isBefore(deletedAt)) {
                throw new IllegalStateException("불변식 위반: purgeAt < deletedAt");
            }
        }
        if (version < 0) {
            throw new IllegalStateException("불변식 위반: version < 0");
        }
    }

    public void delete() {
        applyDelete(null);
    }

    public void delete(Instant purgeAt) {
        applyDelete(purgeAt);
    }

    private void applyDelete(Instant purgeAt) {
        boolean changed = false;

        if (!this.deleted) {
            this.deleted = true;
            this.deletedAt = now();
            changed = true;
        }
        if (!Objects.equals(this.purgeAt, purgeAt)) {
            this.purgeAt = purgeAt;
            changed = true;
        }
        touchIf(changed);
        assertInvariants();
    }

    public void restore() {
        if (this.deleted || this.deletedAt != null || this.purgeAt != null) {
            this.deleted = false;
            this.deletedAt = null;
            this.purgeAt = null;
            touch();
            assertInvariants();
        }
    }

    public void schedulePurge(Instant purgeAt) {
        if (purgeAt == null) {
            throw new IllegalArgumentException("purgeAt 는 null 일 수 없습니다.");
        }
        if (!this.deleted) {
            throw new IllegalStateException("삭제되지 않은 엔티티에 대해 파기 예약은 허용되지 않습니다. 먼저 delete()를 호출하세요.");
        }
        if (purgeAt.isBefore(this.deletedAt)) {
            throw new IllegalArgumentException("purgeAt은 deletedAt 이전일 수 없습니다.");
        }
        if (!purgeAt.equals(this.purgeAt)) {
            this.purgeAt = purgeAt;
            touch();
        }
        assertInvariants();
    }

    public void cancelPurge() {
        if (this.purgeAt != null) {
            this.purgeAt = null;
            touch();
            assertInvariants();
        }
    }

    public boolean shouldPurge() {
        return shouldPurge(now());
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
