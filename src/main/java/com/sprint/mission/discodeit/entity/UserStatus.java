package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {
    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId) {
        this(UUID.randomUUID(), Instant.now(), userId, Instant.now());
    }

    public UserStatus(UUID id, Instant createAt, UUID userId, Instant lastActiveAt) {
        super(id, createAt);
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public void login() {
        lastActiveAt = Instant.now();
        updateTimeStamp();
    }

    public boolean isOnline() {
        return lastActiveAt.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
    }
}
