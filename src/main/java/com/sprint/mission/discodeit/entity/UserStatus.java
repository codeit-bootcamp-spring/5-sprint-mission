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

    public boolean isOnline() {
        return Duration.between(lastAccessedAt, Instant.now()).toMinutes() <= 5;
    }
    public UserStatus(UUID id, UUID userId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
