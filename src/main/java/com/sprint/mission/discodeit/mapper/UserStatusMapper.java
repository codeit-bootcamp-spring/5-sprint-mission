package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserStatusMapper {

  public UserStatusResponseDto toDto(UserStatus status) {
    if (status == null) return null;

    return new UserStatusResponseDto(
        status.getId(),
        status.getUser().getId(),
        status.getLastActiveAt(),
        status.getCreatedAt(),
        status.getUpdatedAt()
    );
  }
}

