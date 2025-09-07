package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public boolean isOnline() {
        Duration duration = Duration.between(lastActiveAt, Instant.now());
        return duration.toMinutes() <= 5;
    }

    public void update(Instant lastAccessAt) {
        boolean anyValueUpdated = false;
        if (lastAccessAt != null && !lastAccessAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastAccessAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.setUpdatedAt(Instant.now());
        }
    }

    @Override
    public String toString() {
        return super.toString() + " UserStatus{" +
                "userId=" + userId +
                ", lastActiveAt=" + lastActiveAt +
                '}';
    }
}
