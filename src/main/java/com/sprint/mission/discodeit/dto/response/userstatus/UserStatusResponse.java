package com.sprint.mission.discodeit.dto.response.userstatus;

import com.sprint.mission.discodeit.domain.entity.UserStatus;

import java.util.UUID;

public record UserStatusResponse(
        UUID userId,
        String displayName
) {

    public static UserStatusResponse from(UserStatus userStatus) {
        return new UserStatusResponse(
                userStatus.getUserId(),
                userStatus.getType().getDisplayName()
        );
    }
}
