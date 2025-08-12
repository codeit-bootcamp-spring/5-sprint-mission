package com.sprint.mission.discodeit.dto.user;

import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@ToString
@Data
public class UserResponse {

    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;

    private final String userId;
    private final String email;

    private final boolean isOnline;
    private final Instant lastOnline;

    public UserResponse(UUID id, Instant createdAt, Instant updatedAt, String userId, String email, boolean isOnline, Instant lastOnline) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.email = email;
        this.isOnline = isOnline;
        this.lastOnline = lastOnline;
    }

    public void updateTime() {
    }
}
