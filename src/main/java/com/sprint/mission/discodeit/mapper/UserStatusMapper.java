package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {

  public UserStatusDto.Detail toDetail(UserStatus userStatus) {
    if (userStatus == null) {
      return null;
    }

    return UserStatusDto.Detail.builder()
                               .id(userStatus.getId())
                               .userId(userStatus.getUser()
                                                 .getId())
                               .lastActiveAt(userStatus.getLastActiveAt())
                               .build();
  }
}
