package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.data.UserStatusDto;
import com.codeit.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.codeit.mission.discodeit.dto.request.UserStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatusDto create(UserStatusCreateRequest request);

    UserStatusDto find(UUID userStatusId);

    List<UserStatusDto> findAll();

    UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request);

    UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);

    void delete(UUID userStatusId);
}
