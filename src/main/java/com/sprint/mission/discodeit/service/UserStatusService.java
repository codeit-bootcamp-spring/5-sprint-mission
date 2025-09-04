package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.CreateUserStatusDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateStatusByUserIdDTO;
import com.sprint.mission.discodeit.domain.dto.userStatus.UserStatusDto;

public interface UserStatusService {

	public UserStatusDto create(CreateUserStatusDTO dto);

	public UserStatusDto find(UUID id);

	public List<UserStatusDto> findAll();

	public UserStatusDto updateStatusByUserId(UpdateStatusByUserIdDTO dto);

	public void delete(UUID id);
}
