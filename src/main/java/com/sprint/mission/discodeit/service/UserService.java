package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void createUser(User user);

    void updateUser(User user);

    void deleteUser(User user);

    User searchByIndex(int i);

    User searchById(UUID id);

    List<User> searchByName(String name);

    List<User> getAllUsers();
}
