package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        UUID channelId,
        UUID senderId,
        String content,
        Set<UUID> attachmentIds,
        UUID replyTo
) {
}
