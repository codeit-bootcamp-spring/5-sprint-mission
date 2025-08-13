package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserCreateResponse;
import com.sprint.mission.discodeit.dto.response.UserFindResponse;
import com.sprint.mission.discodeit.dto.response.UserUpdateResponse;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserCreateResponse create(String username, String email, String password, String fileName);
    User create(String username, String email, String password);
//    UserCreateResponse create(String username, String email, String password);
//    UserCreateResponse create(UserCreateRequest userCreateRequest);
//    UserCreateResponse create(UserCreateRequest userCreateRequest,String fileName);
    User create(UserCreateRequest userCreateRequest);
    User create(UserCreateRequest userCreateRequest,String fileName);
    Optional<UserFindResponse> find(UUID userId);
    List<UserFindResponse> findAll();
    User update(UUID userId, String newUsername, String newEmail, String newPassword);
    UserUpdateResponse update(UserUpdateRequest userUpdateRequest);
    void delete(UUID userId);
}
