package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        Instant createdAt,
        Instant updatedAt,
        boolean online
) {
    public static UserResponseDto fromEntity(User user, UserStatus status) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                status != null && status.isOnline()
        );
    }
}
