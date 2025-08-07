package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class UserStatus {
    private final UUID id;
    private final UUID userId;
    private final Instant createdAt;
    private final Instant updatedAt;
    private Instant lastAccessedAt;

    public UserStatus(UUID id, UUID userId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ✅ 추가된 생성자
    public UserStatus(UUID userId, Instant lastAccessedAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastAccessedAt = lastAccessedAt;
    }

    public void update(Instant newAccessTime) {
        this.lastAccessedAt = newAccessTime;
    }

    public void update() {
        this.lastAccessedAt = Instant.now();
    }

    public boolean isOnline() {
        return Duration.between(lastAccessedAt, Instant.now()).toMinutes() <= 5;
    }
}

