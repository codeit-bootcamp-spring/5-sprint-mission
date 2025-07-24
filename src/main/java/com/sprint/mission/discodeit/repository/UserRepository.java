package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void save(User user);

    void delete(User user);

    void deleteAll();

    Optional<User> searchById(UUID id);

    List<User> searchByName(String name);

    List<User> searchAll();
}
