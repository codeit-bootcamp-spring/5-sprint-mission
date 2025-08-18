package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.UUID;

public class UserResponse {

    @Builder
    public record detail(
            UUID id,
            String name,
            String email,
            @Nullable UUID imageId,
            @Nullable String imageUrl,
            String createdAt,
            String updatedAt,
            Boolean online
    ) {}

    @Builder
    public record summary(
            UUID id,
            String name,
            String email,
            @Nullable UUID imageId,
            @Nullable String imageUrl,
            Boolean online
    ) {}
}
