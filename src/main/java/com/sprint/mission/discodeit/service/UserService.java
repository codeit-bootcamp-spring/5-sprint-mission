package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse create(UserCreateRequest request);
    UserResponse find(UUID userId);
    List<UserResponse> findAll();
    boolean update(UserUpdateRequest request);
    void delete(UUID userId);
}

