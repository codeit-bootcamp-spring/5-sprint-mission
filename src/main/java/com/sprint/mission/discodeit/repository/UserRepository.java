package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  @EntityGraph(attributePaths = {"profile", "status"})
  List<User> findAll();

  @EntityGraph(attributePaths = {"profile", "status"})
  Optional<User> findById(UUID userId);

  @EntityGraph(attributePaths = {"profile", "status"})
  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
