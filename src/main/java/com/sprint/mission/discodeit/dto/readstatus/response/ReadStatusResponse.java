package com.sprint.mission.discodeit.dto.readstatus.response;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponse(
        UUID userId,
        UUID channelId,
        Instant readAt
) {
}
