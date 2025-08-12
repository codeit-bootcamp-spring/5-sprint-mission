package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record BinaryContentResponse(
        UUID id,
        UUID messageId,
        String fileName,
        String contentType,
        long size
) {
}
