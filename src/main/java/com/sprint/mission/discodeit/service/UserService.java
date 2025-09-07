package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserCreateRequest request, Optional<BinaryContentCreateRequest> optionalProfile);
    UserResponseDto find(UUID userId);
    List<UserResponseDto> findAll();
    UserResponseDto update(UUID userId, UserUpdateRequest request, Optional<BinaryContentCreateRequest> optionalProfile);
    void delete(UUID userId);
}
