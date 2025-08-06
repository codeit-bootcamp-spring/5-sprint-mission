package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserRegisterDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User create(UserRegisterDto dto);

    List<User> findAll();

    User findById(UUID id);

    User findByEmail(String email);

    User update(UserUpdateDto dto);

    User updatePassword(UUID userId, String currentPassword, String newPassword);

    boolean delete(UUID id);

}
