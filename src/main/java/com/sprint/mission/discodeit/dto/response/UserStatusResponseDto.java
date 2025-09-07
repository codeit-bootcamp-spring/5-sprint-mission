package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDto(
        UUID id,
        UUID userId,
        Instant lastActiveAt,
        Instant createdAt,
        Instant updatedAt
) {
}
