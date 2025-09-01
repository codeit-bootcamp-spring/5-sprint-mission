package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.CreateUserDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateUserDTO;
import com.sprint.mission.discodeit.domain.dto.UserDeleteResult;
import com.sprint.mission.discodeit.domain.dto.UserReadResult;
import com.sprint.mission.discodeit.domain.dto.UserUpdateResult;
import com.sprint.mission.discodeit.domain.entity.User;

public interface UserService {
	User create(CreateUserDTO dto);

	List<UserReadResult> readAll();

	UserDeleteResult delete(UUID userId);

	UserUpdateResult update(UpdateUserDTO dto);

	UserReadResult read(UUID userId);

	boolean isEmpty(UUID userId);

	void deleteAll();

	boolean isOnline(UUID id);

}
