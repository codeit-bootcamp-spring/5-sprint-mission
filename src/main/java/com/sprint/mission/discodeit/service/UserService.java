package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(User user);

    List<User> getAll();

    User get(UUID id);

    User update(UUID id, String name, boolean isOnline);

    void delete(UUID id);

    void deleteAll();
}
