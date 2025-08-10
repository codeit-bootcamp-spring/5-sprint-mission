package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final long ONLINE_WINDOW_MINUTES = 5L;

    private final UUID id;
    private final UUID userId;
    private final Instant createdAt;

    private Instant updatedAt;
    private Instant lastAccessedAt;

    public UserStatus(UUID id, UUID userId, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
        this.updatedAt = this.createdAt;
        this.lastAccessedAt = this.updatedAt;
    }

    public UserStatus(UUID userId, Instant lastOnlineAt) {
        this(UUID.randomUUID(), userId, Instant.now(), Instant.now());
        this.lastAccessedAt = (lastOnlineAt == null ? this.updatedAt : lastOnlineAt);
    }

    public boolean isOnline() {
        return Duration.between(lastAccessedAt, Instant.now()).toMinutes() <= ONLINE_WINDOW_MINUTES;
    }

    public void update() {
        this.lastAccessedAt = Instant.now();
        this.updatedAt = this.lastAccessedAt;
    }

    public void update(Instant when) {
        this.lastAccessedAt = (when == null ? Instant.now() : when);
        this.updatedAt = Instant.now();
    }
}
