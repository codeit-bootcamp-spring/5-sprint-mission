package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final BinaryContentMapper binaryContentMapper;

  public UserDto toDto(User u) {
    if (u == null) return null;
    return new UserDto(
        u.getId(),
        u.getUsername(),
        u.getEmail(),
        binaryContentMapper.toDto(u.getProfile()),
        (u.getStatus() != null) && u.getStatus().isOnline()
    );
  }
}
