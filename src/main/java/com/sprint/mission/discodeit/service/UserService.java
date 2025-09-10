package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto create(UserCreateRequest userCreateRequest, BinaryContentCreateRequest binaryContentCreateRequest);
    UserDto find(UUID id);
    List<UserDto> findAll();
    UserDto update(UUID id, UserUpdateRequest userUpdateRequest, BinaryContentCreateRequest binaryContentCreateRequest);
    void delete(UUID id);
}
