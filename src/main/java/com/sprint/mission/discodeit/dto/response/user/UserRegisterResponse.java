package com.sprint.mission.discodeit.dto.response.user;

import com.sprint.mission.discodeit.domain.entity.User;
import java.util.UUID;

public record UserRegisterResponse(
    UUID id,
    String email,
    String username,
    String globalName
) {

  public static UserRegisterResponse from(User user) {
    return new UserRegisterResponse(
        user.getId(),
        user.getEmail(),
        user.getUsername(),
        user.getGlobalName()
    );
  }
}
