package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record BinaryContentResponse(
        UUID id,
        String filename,
        String contentType,
        long size
) {
}
