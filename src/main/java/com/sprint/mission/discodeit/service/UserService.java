package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void create(User user);
    User read(UUID id);
    List<User> readAll();
    void update(UUID id, String newName);
    void delete(UUID id);
}


