package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {
  private final BinaryContentMapper binaryContentMapper;

  public UserResponseDto toDto(User user, UserStatus status) {
    BinaryContentResponse profileDto = binaryContentMapper.toDto(user.getProfile());
    return new UserResponseDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        profileDto,
        status != null && status.isOnline()
    );
  }
}
