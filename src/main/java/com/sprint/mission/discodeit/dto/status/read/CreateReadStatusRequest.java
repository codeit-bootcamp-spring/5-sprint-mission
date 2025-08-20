package com.sprint.mission.discodeit.dto.status.read;

import java.time.Instant;
import java.util.UUID;

public record CreateReadStatusRequest(
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {}
