package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;
import java.util.UUID;

public class ReadStatusRequest {

    public record Create(
        UUID userId,
        UUID channelId,
        Instant lastReadAt
    ) {}

    public record Update(
            Instant newLastReadAt
    ) {}
}
