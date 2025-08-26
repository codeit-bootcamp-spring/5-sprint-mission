package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.User;
import java.util.Optional;

public interface UserRepository extends AbstractRepository<User> {

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
