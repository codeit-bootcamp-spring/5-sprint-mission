package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  // ---- 명시적 CRUD 재선언 (JpaRepository가 이미 제공하지만 과제 요구에 맞춰 표시) ----
  @Override
  User save(User user);

  @Override
  Optional<User> findById(UUID id);

  @Override
  List<User> findAll();

  @Override
  boolean existsById(UUID id);

  @Override
  void deleteById(UUID id);

  // ---- 커스텀 쿼리 메서드 ----
  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
