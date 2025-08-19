package com.sprint.mission.discodeit.dto.response.user;

import com.sprint.mission.discodeit.domain.entity.User;
import java.time.Instant;
import java.util.UUID;

public record UserSaveResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    String password,
    UUID profileId
) {

  public static UserSaveResponse from(User u) {
    return new UserSaveResponse(
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
