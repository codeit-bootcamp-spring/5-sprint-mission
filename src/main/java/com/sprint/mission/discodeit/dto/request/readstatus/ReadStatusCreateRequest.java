package com.sprint.mission.discodeit.dto.request.readstatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId,
        UUID channelId,
        Instant lastReadAt // TODO 왜 필요한지?
) {
}
