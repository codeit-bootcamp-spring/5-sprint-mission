package com.sprint.mission.discodeit.dto.request;


public record BinaryContentCreateRequest(
        byte[] data,
        String filename,
        String contentType,
        Long size
) {}
