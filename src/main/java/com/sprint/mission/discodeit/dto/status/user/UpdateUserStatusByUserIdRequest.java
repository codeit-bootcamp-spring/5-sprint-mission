package com.sprint.mission.discodeit.dto.status.user;

import java.time.Instant;
import java.util.UUID;

public record UpdateUserStatusByUserIdRequest(
        UUID userId,
        Boolean online,
        Instant lastActiveAt
) {}
