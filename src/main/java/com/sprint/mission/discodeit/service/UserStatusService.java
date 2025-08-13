package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.status.user.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.status.user.UpdateUserStatusByUserIdRequest;
import com.sprint.mission.discodeit.dto.status.user.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.status.user.UserStatusResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(CreateUserStatusRequest request);
    Optional<UserStatusResponse> findById(UUID id);
    List<UserStatusResponse> findAll();
    UserStatusResponse update(UpdateUserStatusRequest request);
    UserStatusResponse updateByUserId(UpdateUserStatusByUserIdRequest request);
    boolean remove(UUID id);
}
