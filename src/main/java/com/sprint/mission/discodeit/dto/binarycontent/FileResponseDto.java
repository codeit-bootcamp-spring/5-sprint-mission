package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.UUID;

public record FileResponseDto(
        UUID id,
        String name,
        String contentType,
        long size,
        String url
) {}
