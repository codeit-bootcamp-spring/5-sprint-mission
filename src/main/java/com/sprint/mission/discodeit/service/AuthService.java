package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.dto.command.UpdateRoleCommand;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;

public interface AuthService {

	UserDto updateRole(UpdateRoleCommand command);

}
