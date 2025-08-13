package com.codeit.mission.discodeit.dto.userstatus;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class UserStatusCreateRequest {

    private final Instant lastAccessTime;
    private final UUID userId;

    public UserStatusCreateRequest(Instant lastAccessTime, UUID userId) {
        this.lastAccessTime = lastAccessTime;
        this.userId = userId;
    }

    public UserStatusCreateRequest(UUID userId) {
        this(Instant.now(), userId);
    }
}
