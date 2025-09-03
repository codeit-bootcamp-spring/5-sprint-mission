package com.codeit.mission.discodeit.mapper;

import com.codeit.mission.discodeit.dto.data.UserStatusDto;
import com.codeit.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {

    public UserStatusDto toDto(UserStatus userStatus) {
        return new UserStatusDto(userStatus.getId(), userStatus.getUser().getId(),
            userStatus.getLastActiveAt());
    }
}
