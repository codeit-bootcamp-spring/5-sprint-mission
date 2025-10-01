package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.CreateUserDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateUserDTO;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;

public interface UserService {
	UserDto create(CreateUserDTO dto);

	List<UserDto> readAll();

	void delete(UUID userId);

	UserDto update(UpdateUserDTO dto);

}
