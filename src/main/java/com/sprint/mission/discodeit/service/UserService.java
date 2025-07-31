package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    void create(User user);
    User read(UUID id);
    List<User> readAll();
    boolean update(UUID id, String newName);

    User create(String name, String email, String password);

    List<User> findAll();

    Optional<User> findById(UUID id);

    void delete(UUID id);
}


