package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.dto.command.GetNewAccTokenCommand;
import com.sprint.mission.discodeit.domain.dto.command.UpdateRoleCommand;
import com.sprint.mission.discodeit.domain.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;

public interface AuthService {

	UserDto updateRole(UpdateRoleCommand command);

	JwtDto getNewAccToken(GetNewAccTokenCommand command);

	UserDto getProfile(String refreshToken);
}
