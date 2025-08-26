package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.userStatus.UserStatusResponse;

public interface UserStatusService {
	UserStatusResponse create(UserStatusCreateRequest request);
	UserStatusResponse getById(UUID id);
	List<UserStatusResponse> getAll();
	UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request);
	UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request);
	UserStatusResponse delete(UUID id);
	boolean isOnline(UUID userId);
}
