package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserStatus extends BaseEntity {
    private UUID userId;
    private Instant lastSeenAt;

    public UserStatus(UUID userId, Instant lastSeenAt) {
        super();
        this.userId = userId;
        this.lastSeenAt = lastSeenAt;
    }

    public boolean isOnline() {
        return lastSeenAt != null &&
                Duration.between(lastSeenAt, Instant.now()).toMinutes() <= 5;
    }

    public void update(Instant newSeenAt) {
        if (newSeenAt.isAfter(this.lastSeenAt)) {
            this.lastSeenAt = newSeenAt;
            this.updatedAt = Instant.now();
        }
    }

}
