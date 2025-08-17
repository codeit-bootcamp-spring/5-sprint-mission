package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(UserStatusCreateRequest request);

    UserStatusResponse find(UUID id);

    List<UserStatusResponse> findAll();

    UserStatusResponse update(UserStatusUpdateRequest request);

    void updateUserStatusByUserId(UUID userId, UserStatusUpdateByUserIdRequest request);

    void delete(UUID id);

    void clear();
}