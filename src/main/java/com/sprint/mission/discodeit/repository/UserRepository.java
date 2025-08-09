package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<User> searchByEmail(String email);

    List<User> searchByUsername(String username);

    List<User> searchByGlobalName(String globalName);
}
