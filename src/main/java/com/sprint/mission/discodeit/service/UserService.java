package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse create(CreateUserRequest dto);

    List<UserListResponse> findAll();

    User findById(UUID id);

    User findByEmail(String email);

    User update(UpdateUserRequest dto);

    User updatePassword(PasswordRequest req);

    boolean delete(UUID id);

}
