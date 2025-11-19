package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.UserRole;

import java.util.UUID;

public interface AuthService {
    UserDto updateUserRole(UUID userId, UserRole role);
}
