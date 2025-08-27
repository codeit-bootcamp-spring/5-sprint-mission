package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatus create(@Valid UserStatusCreateRequest userStatusCreateRequest);

  UserStatus findById(UUID id);

  UserStatus findByUserId(UUID userid);

  List<UserStatus> findAll();

  UserStatus update(UUID userStatusId, @Valid UserStatusUpdateRequest userStatusUpdateRequest);

  UserStatus updateByUserId(UUID userId, @Valid UserStatusUpdateRequest userStatusUpdateRequest);

  void delete(UUID id);
}
