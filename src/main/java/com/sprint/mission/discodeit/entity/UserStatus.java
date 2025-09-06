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
    private Instant lastAccessAt;

    public UserStatus(UUID userId, Instant lastAccessAt) {
        this.userId = userId;
        this.lastAccessAt = lastAccessAt;
    }

    public boolean online() {
        Duration duration = Duration.between(lastAccessAt, Instant.now());
        return duration.toMinutes() <= 5;
    }

    public void update(Instant lastAccessAt) {
        boolean anyValueUpdated = false;
        if (lastAccessAt != null && !lastAccessAt.equals(this.lastAccessAt)) {
            this.lastAccessAt = lastAccessAt;
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
                ", lastAccessAt=" + lastAccessAt +
                '}';
    }
}
