package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String username, String email, String password);
    UserResponseDto find(UUID userId);
    List<UserResponseDto> findAll();

    UserResponseDto update(UserUpdateRequest request);

    void delete(UUID userId);
    UserResponseDto create(UserCreateRequest request, @Nullable BinaryContentCreateRequest profileImage);

    void updateLastAccessAt(UUID userId, Instant lastAccessAt);

}
