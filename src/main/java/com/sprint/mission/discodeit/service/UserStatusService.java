package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.userStatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UpdateUserStatusByUserIdRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.response.userStatus.UserStatusResponse;

public interface UserStatusService {
	UserStatusResponse create(CreateUserStatusRequest request);
	UserStatusResponse getById(UUID id);
	List<UserStatusResponse> getAll();
	UserStatusResponse update(UpdateUserStatusRequest request);
	UserStatusResponse updateByUserId(UpdateUserStatusByUserIdRequest request);
	UserStatusResponse delete(UUID id);
}
