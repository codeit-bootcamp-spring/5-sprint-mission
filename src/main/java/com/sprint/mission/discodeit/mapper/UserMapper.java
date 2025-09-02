package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final BinaryContentMapper binaryContentMapper;

  public UserDto.Detail toDetail(User user) {
    if (user == null) {
      return null;
    }

    return UserDto.Detail.builder()
                         .id(user.getId())
                         .username(user.getUsername())
                         .email(user.getEmail())
                         .profile(binaryContentMapper.toDetail(user.getProfile()))
                         .online(user.getStatus() != null && user.getStatus()
                                                                 .isOnline())
                         .build();
  }
}
