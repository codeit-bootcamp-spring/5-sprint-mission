package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatusResponseDto create(UUID userId, UserStatusCreateRequest request);

    UserStatusResponseDto find(UUID id);

    List<UserStatusResponseDto> findAll();

    UserStatusResponseDto update(UUID userStatusId, UserStatusUpdateRequest request);

    UserStatusResponseDto updateByUserId(UUID userId, UserStatusUpdateRequest request);

    void delete(UUID id);
}
