package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserUpdateResponse(
        UUID id,
        String name,
        String email,
        Instant createdAt,
        Instant updatedAt,
        UUID profileId
) {
}
