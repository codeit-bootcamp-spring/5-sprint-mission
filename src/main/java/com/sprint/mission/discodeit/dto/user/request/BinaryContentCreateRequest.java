package com.sprint.mission.discodeit.dto.user.request;

public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {}
