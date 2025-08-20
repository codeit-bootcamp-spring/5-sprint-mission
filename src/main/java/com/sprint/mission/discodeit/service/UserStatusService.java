package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusCreateRequest userStatusCreateRequest);
    UserStatus find(UUID userStatusId);
    List<UserStatus> findAll();
    UserStatus update(UUID userStatusId, UserStatusUpdateRequest userStatusUpdateRequest);
    UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest userStatusUpdateRequest);
    void delete(UUID userStatusId);
}
