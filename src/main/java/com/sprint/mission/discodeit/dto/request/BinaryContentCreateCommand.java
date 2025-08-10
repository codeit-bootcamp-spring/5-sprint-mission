package com.sprint.mission.discodeit.dto.request;

public record BinaryContentCreateCommand(
        String filename,
        String contentType,
        byte[] data
) {
}
