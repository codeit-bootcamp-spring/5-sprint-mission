package com.sprint.mission.discodeit.dto.user;

public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {}
