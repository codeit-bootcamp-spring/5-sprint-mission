package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;


/* 응답용 record
 *자동으로 getter, toString, equals, hashCode,
 *생성자 다 만들어줌
 */

public record UserDto(
    UUID id,
    String username,
    String email,
    UUID profileId,
    Boolean online
) {

  //Entity에서 Dto로 변환
  public static UserDto fromEntity(User user) {
    // UserStatus의 NULL 체크
    boolean online = user.getStatus() != null && user.getStatus().isOnline();
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        (user.getProfile() != null) ? user.getProfile().getId() : null,
        online
    );
  }
}

