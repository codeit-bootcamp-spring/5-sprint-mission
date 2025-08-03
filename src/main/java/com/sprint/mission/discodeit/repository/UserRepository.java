package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(UUID id);

    List<User> findAll();

    long count();

    boolean delete(UUID id);

    boolean existsById(UUID id);

    boolean update(UUID id,String username, String password);
}
