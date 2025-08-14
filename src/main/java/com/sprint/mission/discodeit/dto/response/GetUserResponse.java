package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record GetUserResponse(
        UUID userId,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        boolean online
) {}
