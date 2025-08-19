package com.sprint.mission.discodeit.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record FileResponseDto(
        UUID id,
        Instant createdAt,
        String fileName,
        String contentType,
        long size,
        String bytes
) {}
