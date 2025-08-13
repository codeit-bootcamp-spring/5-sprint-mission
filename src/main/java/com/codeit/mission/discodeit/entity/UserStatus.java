package com.codeit.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class UserStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;

    private Instant lastAccessTime;

    private static final long ONLINE_THRESHOLD_MILLIS = 5 * 60 * 1000;

    public UserStatus(UUID userId, Instant lastAccessTime) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.userId = userId;
        this.lastAccessTime = lastAccessTime;
    }

    public void update(Instant newLastAccessTime) {
        boolean anyValueUpdated = false;
        if (newLastAccessTime != null && !newLastAccessTime.equals(this.lastAccessTime)) {
            this.lastAccessTime = newLastAccessTime;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public boolean isOnline() {
        if (lastAccessTime == null) {
            return false;
        }

        Duration timeDiff = Duration.between(lastAccessTime, Instant.now());
        return timeDiff.toMillis() <= ONLINE_THRESHOLD_MILLIS;
    }
}