package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse.detail create(UserRequest.create dto);

    User update(UserRequest.update dto);

    User updatePassword(UserRequest.passwordReset dto);

    List<UserResponse.summary> findAll();

    User findById(UUID id);

    User findByEmail(String email);

    boolean delete(UUID id);

}
