package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;

public record UserStatusResponse(
    UUID id,
    UUID userId,
    Instant lastActiveAt,
    Boolean online
) {
    public UserStatusResponse(UserStatus userStatus) {
        this(userStatus.getId(), userStatus.getUserId(), userStatus.getLastActiveAt(), userStatus.isOnline());
    }
}
