package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface UserService {
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(UUID id);
    User getUser(UUID id);
    HashMap<UUID, User> getAllUsers();
}
