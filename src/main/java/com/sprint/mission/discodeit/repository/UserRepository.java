package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
<<<<<<< HEAD
    Optional<User> find(UUID userId);
    List<User> findAll();
    boolean existById(UUID userId);
    void delete(UUID userId);
=======
    Optional<User> findById(UUID id);
    List<User> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
>>>>>>> 717adae (feat: 초기 커밋)
}
