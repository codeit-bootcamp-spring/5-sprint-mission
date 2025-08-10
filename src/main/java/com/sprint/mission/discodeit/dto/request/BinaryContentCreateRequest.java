package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record BinaryContentCreateRequest(
        UUID messageId,
        UUID userId,
        byte[] data,
        String fileName,
        String contentType
){
}
