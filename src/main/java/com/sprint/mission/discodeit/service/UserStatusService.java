package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatusDto create(@Valid UserStatusCreateRequest userStatusCreateRequest);

  UserStatusDto findById(UUID id);

  UserStatusDto findByUserId(UUID userid);

  List<UserStatusDto> findAll();

  UserStatusDto update(UUID userStatusId, @Valid UserStatusUpdateRequest userStatusUpdateRequest);

  UserStatusDto updateByUserId(UUID userId, @Valid UserStatusUpdateRequest userStatusUpdateRequest);

  void delete(UUID id);
}
