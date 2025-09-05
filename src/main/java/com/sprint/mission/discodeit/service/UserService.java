package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

  UserDto create(UserCreateRequest req, Optional<BinaryContentCreateRequest> profileReq);
  UserDto update(UUID userId, UserUpdateRequest req, Optional<BinaryContentCreateRequest> profileReq);
  List<UserDto> findAll();
  UserDto find(UUID userId);
  void delete(UUID userId);

}