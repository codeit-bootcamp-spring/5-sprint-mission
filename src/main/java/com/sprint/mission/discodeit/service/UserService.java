package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AddUserDto;
import com.sprint.mission.discodeit.dto.response.GetUserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User addUser(AddUserDto addUserDto);
    GetUserDto getUserById(UUID userId);
    List<GetUserDto> getAllUser();
    User updateUser(UUID userId, AddUserDto addUserDto);
    void deleteUser(UUID userId);
    void deleteAllUser();
}
