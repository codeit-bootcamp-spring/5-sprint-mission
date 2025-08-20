package com.sprint.mission.discodeit.dto.response.user;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.enums.UserStatusType;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String email,
        String username,
        String globalName,
        String bio,
        UUID profileId,
        UserStatusType userStatusType
) {

    public static UserResponse from(User user, UserStatusType status) {
        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getEmail(),
                user.getUsername(),
                user.getGlobalName(),
                user.getBio(),
                user.getProfileId(),
                status
        );
    }
}
