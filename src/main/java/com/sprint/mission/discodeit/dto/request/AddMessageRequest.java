package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record AddMessageRequest(
        String messageContent,
        UUID userId,
        UUID channelId,
        UUID... attachmentIds
) {
}
