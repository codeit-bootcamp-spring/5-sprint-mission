package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface UserStatusService {

    boolean isActive(UUID userId);
    void updateLastActive(User user);
    void create(UserStatusCreateDto dto);
    void findById(UUID id);
    void findAllByUserId(UUID userId);
    void deleteById(UUID id);
}
