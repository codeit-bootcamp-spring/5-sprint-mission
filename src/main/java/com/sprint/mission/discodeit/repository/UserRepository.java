package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(String email);     // ← 위 코드에서 사용
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
}
