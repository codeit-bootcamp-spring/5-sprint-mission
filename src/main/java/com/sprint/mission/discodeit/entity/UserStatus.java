package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    public static final Duration ONLINE_TIMEOUT = Duration.ofMinutes(5);

    private final UUID id;
    private final UUID userId;
    private volatile Instant lastAt;

    private Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID id, UUID userId, Instant lastAt) {
        this.id = id;
        this.userId = userId;

        this.createdAt = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        this.updatedAt = createdAt;
    }

    public boolean isOnline() {
        return isOnline(Instant.now());
    }

    public boolean isOnline(Instant now) {
        if (lastAt == null) {
            return false;
        }
        return !now.isBefore(lastAt) && Duration.between(lastAt, now).compareTo(ONLINE_TIMEOUT) <= 0;
    }

    public void markOnlineNow() {
        this.lastAt = Instant.now();
    }

    public void markOnline(Instant now) {
        this.lastAt = Objects.requireNonNull(now, "now");
    }
}
