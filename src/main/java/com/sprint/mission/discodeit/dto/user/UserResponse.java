package com.sprint.mission.discodeit.dto.user;

import org.springframework.lang.Nullable;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        @Nullable UUID imageId,
        @Nullable String imageUrl,
        @Nullable Integer imageSize,
        @Nullable String imageContentType
) {
}
