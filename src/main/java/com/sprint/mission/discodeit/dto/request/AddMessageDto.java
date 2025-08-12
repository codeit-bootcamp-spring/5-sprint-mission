package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record AddMessageDto(
        String messageContent,
        UUID userId,
        UUID channelId,
        UUID... attachmentIds
) {
}
