package com.sprint.mission.discodeit.dto.status.user;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponse(
        UUID id,
        UUID userId,
        boolean online,
        Instant lastActiveAt
) {}
