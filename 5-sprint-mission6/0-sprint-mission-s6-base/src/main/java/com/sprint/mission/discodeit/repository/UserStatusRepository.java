package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  // ---- CRUD (JpaRepository가 제공하지만 과제 요구에 맞춰 명시) ----
  @Override
  UserStatus save(UserStatus userStatus);

  @Override
  Optional<UserStatus> findById(UUID id);

  @Override
  List<UserStatus> findAll();

  @Override
  boolean existsById(UUID id);

  @Override
  void deleteById(UUID id);

}
