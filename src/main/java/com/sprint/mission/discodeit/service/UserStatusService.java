package com.sprint.mission.discodeit.service;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;
=======
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

<<<<<<< HEAD
    UserStatusResponseDto create(UserStatusCreateRequest request);

    UserStatusResponseDto find(UUID id);

    List<UserStatusResponseDto> findAll();

    UserStatusResponseDto update(UUID userStatusId, UserStatusUpdateRequest request);

    UserStatusResponseDto updateByUserId(UUID userId, UserStatusUpdateRequest request);

    void delete(UUID id);
=======
  UserStatusDto create(UserStatusCreateRequest request);

  UserStatusDto find(UUID userStatusId);

  List<UserStatusDto> findAll();

  UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request);

  UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);

  void delete(UUID userStatusId);
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}
