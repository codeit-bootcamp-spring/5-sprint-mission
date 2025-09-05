package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatus create(UserStatusCreateRequest request);

  UserStatus find(UUID userStatusId);

  List<UserStatus> findAll();

  UserStatus update(UUID userStatusId, UserStatusUpdateRequest request);

  /** 없으면 생성하고, 있으면 lastActiveAt 갱신(업서트). request(바디) 생략 가능 */
  UserStatus updateByUserId(UUID userId, @Nullable UserStatusUpdateRequest request);

  void delete(UUID userStatusId);
}
