package com.codeit.mission.discodeit.dto.user;

import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.entity.UserStatus;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class UserResponse {

    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;

    private final String username;
    private final String email;

    private final boolean isOnline;

    private final Instant lastAccessTime;

    private final UUID profileId;

    public UserResponse(User user, UserStatus userStatus, UUID profileId) {
        this.id = user.getId();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.isOnline = userStatus != null && userStatus.isOnline();
        this.lastAccessTime = userStatus != null ? userStatus.getLastAccessTime() : null;
        this.profileId = profileId;
    }

    public UserResponse(User user, UserStatus userStatus) {
        this(user, userStatus, null);
    }
}
