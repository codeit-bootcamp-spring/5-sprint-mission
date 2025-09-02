package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserCreateRequest userCreateRequest, BinaryContentCreateRequest binaryContentCreateRequest);
    UserDto find(UUID id);
    List<UserDto> findAll();
    User update(UUID id, UserUpdateRequest userUpdateRequest, BinaryContentCreateRequest binaryContentCreateRequest);
    void delete(UUID id);
}
