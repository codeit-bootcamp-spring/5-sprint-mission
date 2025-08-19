package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserFindResponse;
import com.sprint.mission.discodeit.entity.User;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

  User create(@Valid UserCreateRequest userCreateRequest,
      @Valid Optional<BinaryContentCreateRequest> binaryContentCreateRequest);

  UserFindResponse findById(UUID userId);

  List<UserFindResponse> findAll();

  User update(@Valid UserUpdateRequest userUpdateRequest,
      @Valid Optional<BinaryContentCreateRequest> binaryContentCreateRequest);

  void delete(UUID userId);
}
