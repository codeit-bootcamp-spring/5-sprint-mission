package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class UserStatus implements Serializable {

    private UUID id;
    private UUID userId;
    private Instant lastOnlineTime;
    private Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId, Instant lastOnlineTime) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.lastOnlineTime = lastOnlineTime;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void updateLastOnlineTime(Instant lastOnlineTime) {
        if (lastOnlineTime != null && !lastOnlineTime.equals(this.lastOnlineTime)) {
            this.lastOnlineTime = lastOnlineTime;
            this.updatedAt = Instant.now();
        }
    }

    public boolean isOnline() {
        return Duration.between(this.lastOnlineTime, Instant.now()).toMinutes() < 5;
    }
}
