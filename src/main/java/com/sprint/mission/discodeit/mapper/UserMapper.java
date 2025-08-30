package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;

@Mapper
@AllArgsConstructor
public class UserMapper {

  private final BinaryContentMapper binaryContentMapper;

  public UserDto toDto(User user) {
    return UserDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .profile(binaryContentMapper.toDto(user.getProfile()))
        .online(user.getStatus().isOnline())
        .build();
  }
}
