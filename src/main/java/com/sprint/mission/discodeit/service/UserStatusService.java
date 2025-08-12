package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusCreateRequest userStatusCreateRequest);

    UserStatus findById(UUID id);

    UserStatus findByUserId(UUID userid);

    List<UserStatus> findAll();

    UserStatus update(UserStatusUpdateRequest userStatusUpdateRequest);

    UserStatus updateByUserId(UserStatusUpdateRequest userStatusUpdateRequest);

    void delete(UUID id);
}
