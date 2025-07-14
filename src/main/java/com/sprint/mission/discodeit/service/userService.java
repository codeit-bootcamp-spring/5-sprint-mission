package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface userService {

    void createUser(User user);

    User getUserById(UUID userId);

    List<User> getAllUsers();

    void updateUser(UUID userId, User user);

    void updateUserUpdatedAt(UUID userId, long updatedAt);

    void deleteUser(UUID userId);
}
