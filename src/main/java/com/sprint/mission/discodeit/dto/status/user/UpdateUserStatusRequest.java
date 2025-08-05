package com.sprint.mission.discodeit.dto.status.user;

import java.time.Instant;
import java.util.UUID;

public record UpdateUserStatusRequest(
        UUID id,
        Boolean online,
        Instant lastActiveAt
) {}
