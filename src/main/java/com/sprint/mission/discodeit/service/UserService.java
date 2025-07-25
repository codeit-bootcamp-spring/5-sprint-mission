package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface UserService {
    User register(String username, String password);
    boolean login();
    User find(UUID userId);
    User findAll();
    User update(UUID userId, String newPassword);
    boolean delete(UUID userId);
}
