package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

  private final BinaryContentMapper binaryContentMapper;

  /** online 값을 알고 있을 때 */
  public UserDto toDto(User user, @Nullable Boolean online) {
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        binaryContentMapper.toDto(user.getProfile()),
        online
    );
  }

  /** online 값을 아직 모르면 null 로 둡니다. */
  public UserDto toDto(User user) {
    return toDto(user, null);
  }
}
