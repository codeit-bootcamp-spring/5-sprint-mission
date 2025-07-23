package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void addUser(User user);
    List<User> getUsers();
    User getUserById(UUID id);
    User getUserByUsername(String username);
    void updateUser(User user, UUID id);
    void deleteUser(UUID id);
    void deleteAll();

}
