package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        Boolean online
) {
    public static UserResponseDto fromEntity(User user, UserStatus status) {
        return new UserResponseDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                status != null && status.isOnline()
        );
    }
}
