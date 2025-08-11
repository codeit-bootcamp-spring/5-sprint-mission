package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID userId;
    private Instant lastAccessAt;

    public UserStatus(UUID userId, Instant lastAccessAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
        this.lastAccessAt = lastAccessAt;
    }

    public void updateLastAccessAt(Instant newAccessTime) {
        if (newAccessTime != null && newAccessTime.isAfter(this.lastAccessAt)) {
            this.lastAccessAt = newAccessTime;
            this.updatedAt = Instant.now();
        }
    }

    public boolean isOnline() {
        return lastAccessAt != null &&
                Duration.between(lastAccessAt, Instant.now()).toMinutes() < 5;
    }
}
