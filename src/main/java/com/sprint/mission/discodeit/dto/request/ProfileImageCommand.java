package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record ProfileImageCommand(
        UUID userId,
        String filename,
        String contentType,
        byte[] data
) {
}
