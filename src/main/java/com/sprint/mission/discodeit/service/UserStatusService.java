package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.*;

public interface UserStatusService {
    UserStatus create(UserStatusCreateRequest request);
    Optional<UserStatus> findById(UUID userStatusId);
    List<UserStatus> findAll();
    UserStatus update(UserStatusUpdateRequest request);
    void delete(UUID userStatusId);
}
