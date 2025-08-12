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

    // ===== Static =====
    @Serial
    private static final long serialVersionUID = 1L;

    // ===== Identity =====
    private final UUID id;

    // ===== Audit =====
    private final Instant createdAt;
    private Instant updatedAt;

    // ===== Soft Delete =====
    private boolean deleted;
    private Instant deletedAt; // 삭제 시각 (soft delete 시 기록)
    private Instant purgeAt;   // 영구 삭제(정리) 예정 시각

    // ===== Concurrency =====
    private long version; // 낙관적 락 버전 (업데이트할 때 증가)

    // ===== Constructors =====
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

    // ===== Domain Behaviors =====

    /**
     * 변경 시각을 갱신하고 버전을 1 증가시킵니다.
     */
    protected void touch() {
        this.updatedAt = Instant.now();
        this.version++;
    }

    /**
     * 소프트 삭제 처리 (purge 예약 없이).
     */
    public void delete() {
        if (!this.deleted) {
            this.deleted = true;
            this.deletedAt = Instant.now();
            this.purgeAt = null;
            touch();
        }
    }

    /**
     * 소프트 삭제 처리 + 영구 삭제 예정 시각을 함께 설정.
     */
    public void delete(Instant purgeAt) {
        if (!this.deleted) {
            this.deleted = true;
            this.deletedAt = Instant.now();
        }
        this.purgeAt = purgeAt;
        touch();
    }

    /**
     * 삭제 상태를 해제하고 관련 필드를 초기화합니다.
     */
    public void restore() {
        if (this.deleted || this.deletedAt != null || this.purgeAt != null) {
            this.deleted = false;
            this.deletedAt = null;
            this.purgeAt = null;
            touch();
        }
    }

    /**
     * 영구 삭제 예정 시각을 설정합니다(보통 삭제 상태에서만 의미 있음).
     */
    public void schedulePurge(Instant when) {
        if (when == null) {
            throw new IllegalArgumentException("purgeAt 는 null 일 수 없습니다.");
        }
        this.purgeAt = when;
        touch();
    }

    /**
     * 영구 삭제 예약을 취소합니다.
     */
    public void cancelPurge() {
        if (this.purgeAt != null) {
            this.purgeAt = null;
            touch();
        }
    }

    /**
     * 현재 시각 기준으로 영구 삭제 대상인지 여부를 반환합니다.
     */
    public boolean shouldPurge() {
        return shouldPurge(Instant.now());
    }

    /**
     * 지정 시각 기준으로 영구 삭제 대상인지 여부를 반환합니다.
     */
    public boolean shouldPurge(Instant now) {
        return this.deleted && this.purgeAt != null && !this.purgeAt.isAfter(now);
    }

    /**
     * 업데이트 전 버전 일치 여부를 확인합니다.
     * 리포지토리 계층에서 기대 버전과 불일치 시 예외를 던져 낙관적 락을 구현할 때 사용합니다.
     */
    public void requireVersion(long expected) {
        if (this.version != expected) {
            throw new IllegalStateException("버전 불일치: expected=" + expected + ", actual=" + this.version);
        }
    }
}
