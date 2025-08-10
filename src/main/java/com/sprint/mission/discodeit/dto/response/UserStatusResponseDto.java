package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDto(
        UUID id,
        UUID userId,
        Instant createdAt,
        Instant updatedAt
) {
}
