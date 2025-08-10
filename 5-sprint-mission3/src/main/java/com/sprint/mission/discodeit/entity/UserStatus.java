package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus {
    private UUID id;
    private Instant updatedAt;
    private Instant createdAt;

    private UUID userId;
    private Instant lastSeenAt;

    public UserStatus(UUID userId, Instant lastSeenAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.lastSeenAt = lastSeenAt;

        this.createdAt = Instant.now();
    }

    public void update(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
        this.updatedAt = Instant.now();
    }

    public boolean isOnLine() {
        return Duration.between(lastSeenAt, Instant.now()).toMinutes() < 5;
    }
}
