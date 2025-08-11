package com.sprint.mission.discodeit.dto.request;

public record BinaryContentCreateRequest(
        String filename,
        String contentType,
        byte[] data
) {
}
