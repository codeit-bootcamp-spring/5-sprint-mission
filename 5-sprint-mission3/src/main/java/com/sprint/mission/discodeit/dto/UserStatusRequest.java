package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusRequest(
        UUID id,
        UUID userId,
        Instant lastSeenAt
) {
}
