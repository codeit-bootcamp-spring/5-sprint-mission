package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;

public interface AuthService {
	UserDto updateRole(UserRoleUpdateRequest request);
}
