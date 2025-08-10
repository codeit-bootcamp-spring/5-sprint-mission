package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.user.UserCreateRequest;
import com.codeit.mission.discodeit.dto.user.UserResponse;
import com.codeit.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(UserCreateRequest request);

    UserResponse find(UUID userId);

    List<UserResponse> findAll();

    UserResponse update(UserUpdateRequest request);

    void delete(UUID userId);
}
