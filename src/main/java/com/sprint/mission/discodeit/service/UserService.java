package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String name, String email, String password);

    User find(UUID userId);

    List<User> findAll();

    User update(UUID userId, String name, String email, String password);

    boolean delete(UUID userId);
}
