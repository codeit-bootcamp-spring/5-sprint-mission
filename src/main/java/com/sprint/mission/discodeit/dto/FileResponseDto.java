package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record FileResponseDto(
        UUID id,
        String name,
        String contentType,
        long size,
        String url
) {}
