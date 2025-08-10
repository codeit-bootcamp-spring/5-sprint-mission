package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID userId;
    private Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID id, UUID userId) {
        this.id = id;
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void update() {
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        return Duration.between(this.updatedAt, Instant.now()).compareTo(Duration.ofMinutes(5)) <= 0;
    }
}