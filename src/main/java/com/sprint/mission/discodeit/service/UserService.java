package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserDto.CreateUser createUser);
    User update(UserDto.UpdateUser updateUser);
    User find(UUID userId);
    List<User> findAll();
    void delete(UUID userId);

}
