package com.sprint.mission.discodeit.dto.binaryContent;

public record FileDto(
        String name,
        String contentType,
        byte[] content ) {}
