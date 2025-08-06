package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String username, String email, String password);

    User find(UUID id);

    List<User> findAll();

    List<User> searchByUsernameOrEmail(String token);

    User update(UUID id, UUID requestId, String newUsername, String newEmail, String newPassword);

    void delete(UUID id, UUID requestId);
}