package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binaryContent.FileDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User create(UserDto userDto, FileDto fileDto);

    User update(UUID id, String name, FileDto fileDto);

    void updatePassword(UUID id, String password, String newPassword);

    List<User> findAll();

    User findById(UUID id);

    User findByEmail(String email);

    boolean delete(UUID id);

}
