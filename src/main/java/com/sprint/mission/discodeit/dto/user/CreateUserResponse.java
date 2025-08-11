package com.sprint.mission.discodeit.dto.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateUserResponse(
        UUID id,
        String name,
        String email,
        String createdAt,
        String updatedAt
) {
}
