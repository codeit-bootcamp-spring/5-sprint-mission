package com.sprint.mission.discodeit.dto.response.user;

import com.sprint.mission.discodeit.domain.entity.User;
import java.time.Instant;
import java.util.UUID;

public record UserCreateResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    String password,
    UUID profileId
) {

  public static UserCreateResponse from(User u) {
    return new UserCreateResponse(
        u.getId(),
        u.getCreatedAt(),
        u.getUpdatedAt(),
        u.getUsername(),
        u.getEmail(),
        u.getPassword(),
        u.getProfileId()
    );
  }
}
