package com.codeit.mission.discodeit.dto.userstatus;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class UserStatusUpdateRequest {

    private final UUID userStatusId;
    private final Instant lastAccessTime;

    public UserStatusUpdateRequest(UUID userStatusId, Instant lastAccessTime) {
        this.userStatusId = userStatusId;
        this.lastAccessTime = lastAccessTime;
    }

    public UserStatusUpdateRequest(UUID userStatusId) {
        this(userStatusId, Instant.now());
    }
}
