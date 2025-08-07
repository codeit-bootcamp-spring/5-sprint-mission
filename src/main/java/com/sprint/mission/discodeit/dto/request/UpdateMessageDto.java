package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record UpdateMessageDto(
        UUID messageId, String messageContent, UUID... attachmentIds
) {
}
