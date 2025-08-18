package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.codeit.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.codeit.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusCreateRequest request);

    UserStatus find(UUID userStatusId);

    List<UserStatus> findAll();

    UserStatus update(UUID userStatusId, UserStatusUpdateRequest request);

    UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request);

    void delete(UUID userStatusId);
}
