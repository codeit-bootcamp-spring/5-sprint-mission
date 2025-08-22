package com.sprint.mission.discodeit.dto.response.user;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    UUID profileId,
    boolean online
) {

  public static UserResponse from(User user, UserStatusType status) {
    return new UserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId(),
        !UserStatusType.OFFLINE.equals(status)
    );
  }
}
