package com.sprint.mission.discodeit.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        Long size,
        byte[] bytes
) {
}
