package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(User user);

    User create(String name, String email, String password);

    List<User> getAll();

    User get(UUID id);

    User update(UUID id, String name, UUID profileId);

    void delete(UUID id);

    void deleteAll();
}
