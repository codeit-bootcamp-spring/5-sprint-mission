package com.sprint.mission.discodeit.service;

import java.util.UUID;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;

public interface AuthService {
	UserDto updateRole(UUID userId, Role role);
}
