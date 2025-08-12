package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.domain.enums.UserStatusType;

import java.util.UUID;

public record UserStatusResponse(
        UUID userId,
        UserStatusType userStatusType
) {
}
