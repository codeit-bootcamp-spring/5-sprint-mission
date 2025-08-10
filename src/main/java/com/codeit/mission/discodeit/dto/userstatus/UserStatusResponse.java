package com.codeit.mission.discodeit.dto.userstatus;

import com.codeit.mission.discodeit.entity.UserStatus;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class UserStatusResponse {
    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;

    private final Instant lastAccessTime;
    private final UUID userId;
    private final boolean isOnline;

    public UserStatusResponse(UserStatus userStatus) {
        this.id = userStatus.getId();
        this.createdAt = userStatus.getCreatedAt();
        this.updatedAt = userStatus.getUpdatedAt();
        this.lastAccessTime = userStatus.getLastAccessTime();
        this.userId = userStatus.getUserId();
        this.isOnline = userStatus.isOnline();
    }
}
