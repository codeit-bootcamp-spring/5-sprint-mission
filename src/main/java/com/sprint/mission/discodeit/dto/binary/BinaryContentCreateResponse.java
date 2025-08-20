package com.sprint.mission.discodeit.dto.binary;

public record BinaryContentCreateResponse(
        String fileName,
        String contentType,
        byte[] bytes
) {
}
