package com.sprint.mission.discodeit.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = {"id", "userId"})
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final Duration ONLINE_TIMEOUT = Duration.ofMinutes(5);

    private final UUID id;
    private final UUID userId;
    private Instant lastActiveAt;

    private final Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;

        this.createdAt = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        this.updatedAt = createdAt;
    }

    public void update(Instant lastActiveAt) {
        boolean anyValueUpdated = false;
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastActiveAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public Boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(ONLINE_TIMEOUT);

        return lastActiveAt.isAfter(instantFiveMinutesAgo);
    }
}
