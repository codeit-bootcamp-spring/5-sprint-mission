package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public record UserFindResponse(
        UUID id,
        String username,
        String email,
        UserStatus userStatus
) {
}
