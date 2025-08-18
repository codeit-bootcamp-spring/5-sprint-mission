package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.dto.request.UserCreateRequest;
import com.codeit.mission.discodeit.dto.request.UserUpdateRequest;
import com.codeit.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest);

    UserDto find(UUID userId);

    List<UserDto> findAll();

    User update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest);

    void delete(UUID userId);
}
