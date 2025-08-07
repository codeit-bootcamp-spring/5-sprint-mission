package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@ToString
public class UserStatus {
    private final UUID id;
    private final UUID userId;
    private final Instant createdAt;
    private Instant lastAccessedAt;

    public boolean isOnline() {
        return Duration.between(lastAccessedAt, Instant.now()).toMinutes() <= 5;
    }
}
