// UserService.java
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void createUser(String username, String password);
    User findUserById(UUID id);
    User findUser(String username);
    List<User> findAllUsers();
    void updateUser(UUID id, String newUsername, String newPassword);
    void deleteUser(UUID id);
}
