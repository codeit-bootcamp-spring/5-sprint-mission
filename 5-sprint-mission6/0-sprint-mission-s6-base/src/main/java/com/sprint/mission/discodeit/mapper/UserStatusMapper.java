package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {

  public UserStatusDto toDto(UserStatus us) {
    if (us == null) return null;
    return new UserStatusDto(
        us.getId(),
        us.getUser().getId(),
        us.getLastActiveAt()
    );
  }
}