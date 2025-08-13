package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ChannelFindResponse(
        UUID channelId,
        Instant lastTime,
        UUID userId

) {
}
