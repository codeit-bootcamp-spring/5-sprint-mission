package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();

    boolean existsById(UUID id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    void deleteById(UUID id);
}
