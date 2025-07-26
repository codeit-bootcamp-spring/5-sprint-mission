package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(User user);

    User update(User user);

    User delete(UUID id);

    void deleteAll();

    Optional<User> searchById(UUID id);

    List<User> searchByName(String name);

    List<User> searchAll();
}
