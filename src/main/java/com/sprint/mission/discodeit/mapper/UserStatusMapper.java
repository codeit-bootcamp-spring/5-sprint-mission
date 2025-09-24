package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {

  public void updateEntityFromDto(UserStatus entity, UserStatusDto dto) {
    if (dto.getNewLastActiveAt() != null) {
      entity.setLastActiveAt(dto.getNewLastActiveAt());
    }
  }
}
