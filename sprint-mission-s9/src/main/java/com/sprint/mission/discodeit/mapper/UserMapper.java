package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
  private final BinaryContentMapper binaryContentMapper;
  private final SessionRegistry sessionRegistry;

  public UserDto toDto(User user) {
    if (user == null) return null;

    BinaryContentDto profileDto = user.getProfile() != null
        ? binaryContentMapper.toDto(user.getProfile())
        : null;

    boolean online = !sessionRegistry.getAllSessions(user.getUsername(), false).isEmpty();

    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        profileDto,
        online,
        user.getRole()
    );
  }
}