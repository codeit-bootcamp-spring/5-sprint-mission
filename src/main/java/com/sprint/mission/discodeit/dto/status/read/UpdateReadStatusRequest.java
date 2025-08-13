package com.sprint.mission.discodeit.dto.status.read;

import java.time.Instant;
import java.util.UUID;

public record UpdateReadStatusRequest(
        UUID id,
        Instant lastReadAt
) {}
