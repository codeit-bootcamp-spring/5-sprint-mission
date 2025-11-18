package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserCommand;

public interface UserService {

	UserDto create(UserCommand userCommand);

	UserDto findById(UUID userId);

	List<UserDto> findAll();

	UserDto update(UUID userId, UserCommand userCommand);

	void delete(UUID userId);
}
