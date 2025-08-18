package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends AbstractRepository<User> {

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  List<User> searchByEmailKeyword(String keyword);

  List<User> searchByUsernameKeyword(String keyword);

  List<User> searchByGlobalNameKeyword(String keyword);

  List<User> searchByEmailPrefix(String prefix);

  List<User> searchByUsernamePrefix(String prefix);

  List<User> searchByGlobalNamePrefix(String prefix);
}
