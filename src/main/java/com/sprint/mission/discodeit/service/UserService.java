package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserCreateRequest request, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);
    UserResponseDto find(UUID userId);
    List<UserResponseDto> findAll();
    UserResponseDto update(UUID userId, UserUpdateRequest request, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);
    void delete(UUID userId);
}
