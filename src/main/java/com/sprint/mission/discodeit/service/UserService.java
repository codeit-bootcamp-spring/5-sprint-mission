// com.sprint.mission.discodeit.service.UserService
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.*;

public interface UserService {
    UUID create(UserCreateRequest request);
    Optional<UserResponse> read(UUID id);
    List<UserResponse> readAll();
    boolean update(UserUpdateRequest request);
    boolean delete(UUID id);

    default UserResponse find(UUID id) {
        return read(id).orElseThrow(() -> new NoSuchElementException("User not found: " + id));
    }
}
