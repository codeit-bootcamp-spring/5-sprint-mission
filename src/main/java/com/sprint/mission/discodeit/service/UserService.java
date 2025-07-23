package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void create(User user);

    void update(User user);

    void delete(User user);

    User searchByIndex(int i);

    User searchById(UUID id);

    List<User> searchByName(String name);

    List<User> searchAll();
}
