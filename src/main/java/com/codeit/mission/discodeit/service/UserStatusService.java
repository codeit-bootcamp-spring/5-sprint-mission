package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.codeit.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.codeit.mission.discodeit.dto.userstatus.UserStatusUpdateByUserRequest;
import com.codeit.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(UserStatusCreateRequest request);

    UserStatusResponse find(UUID userStatusId);

    List<UserStatusResponse> findAll();

    UserStatusResponse update(UserStatusUpdateRequest request);

    UserStatusResponse updateByUserId(UserStatusUpdateByUserRequest request);

    void delete(UUID userStatusId);
}
