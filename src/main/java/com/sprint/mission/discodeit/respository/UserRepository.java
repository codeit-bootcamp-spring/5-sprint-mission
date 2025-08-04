package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    List<User> findAll();

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    Optional<User> update(UUID id, String name);

    boolean delete(UUID id);
}
