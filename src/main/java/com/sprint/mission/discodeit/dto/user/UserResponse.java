package com.sprint.mission.discodeit.dto.user;

import lombok.Builder;
import org.springframework.lang.Nullable;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String name,
        String email,
        @Nullable UUID imageId,
        @Nullable String imageUrl
) {
}
