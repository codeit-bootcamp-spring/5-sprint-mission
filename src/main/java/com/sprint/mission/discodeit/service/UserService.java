package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.PasswordRequest;
import com.sprint.mission.discodeit.dto.user.UserListResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User create(CreateUserRequest dto);

    List<UserListResponse> findAll();

    User findById(UUID id);

    User findByEmail(String email);

    User update(UserUpdateDto dto);

    User updatePassword(PasswordRequest req);

    boolean delete(UUID id);

}
