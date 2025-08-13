package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.reqeust.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.reqeust.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.sub.UserStatus;

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
