package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.domain.enums.UserStatusType;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String email,
        String username,
        String globalName,
        UUID profileId,
        UserStatusType userStatusType
) {
}
