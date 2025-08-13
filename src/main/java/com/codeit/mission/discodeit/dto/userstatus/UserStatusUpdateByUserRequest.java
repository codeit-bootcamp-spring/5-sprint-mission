package com.codeit.mission.discodeit.dto.userstatus;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class UserStatusUpdateByUserRequest {

    private final UUID userId;
    private final Instant lastAccessTime;

    public UserStatusUpdateByUserRequest(UUID userId, Instant lastAccessTime) {
        this.userId = userId;
        this.lastAccessTime = lastAccessTime;
    }

    public UserStatusUpdateByUserRequest(UUID userId) {
        this(userId, Instant.now());
    }
}
