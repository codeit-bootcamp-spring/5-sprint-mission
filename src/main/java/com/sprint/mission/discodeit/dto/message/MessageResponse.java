package com.sprint.mission.discodeit.dto.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID messageId,
        UUID channelId,
        UUID senderId,
        String content,
        List<UUID> attachments,
        Instant createdAt
) {}
