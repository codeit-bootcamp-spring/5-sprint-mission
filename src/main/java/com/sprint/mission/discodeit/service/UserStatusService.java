package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusDto.CreateUserStatus request);
    UserStatus find(UUID id);
    List<UserStatus> findAll();
    UserStatus update(UserStatusDto.UpdateUserStatus request);
}
