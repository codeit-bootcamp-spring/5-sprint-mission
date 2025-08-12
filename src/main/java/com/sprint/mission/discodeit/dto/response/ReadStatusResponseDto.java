package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponseDto(
        UUID id,
        UUID userId,
        UUID channelID,
        Instant createdAt,
        Instant updatedAt
){}
