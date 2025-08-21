package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.UUID;


/* record
 *자동으로 getter, toString, equals, hashCode,
 *생성자 다 만들어줌
 */

public record UserDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    UUID profileId,
    Boolean online
) {


  //컨트롤러 호출용
  public static UserDto fromEntity(User user, boolean online) {
    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId(),
        online
    );
  }

  public static UserDto from(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      UUID profileId,
      String username,
      String email,
      boolean isOnline
  ) {
    return new UserDto(
        id,
        createdAt,
        updatedAt,
        username,
        email,
        profileId,
        isOnline
    );
  }
}


