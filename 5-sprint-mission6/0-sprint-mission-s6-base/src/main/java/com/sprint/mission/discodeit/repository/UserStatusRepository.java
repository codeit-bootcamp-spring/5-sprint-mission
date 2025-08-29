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

  // ---- 커스텀 쿼리 메서드 (연관 엔티티의 ID는 property path 사용) ----
  Optional<UserStatus> findByUser_Id(UUID userId);

  void deleteByUser_Id(UUID userId);
}
