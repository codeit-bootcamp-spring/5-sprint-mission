package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// UserStatus 엔티티에 대한 DB 접근을 담당하는 Repository 인터페이스
// JpaRepository<UserStatus, UUID>를 상속받아 기본 CRUD 메서드 제공
public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

    // 특정 사용자(userId)의 상태(UserStatus)를 조회
    Optional<UserStatus> findByUserId(UUID userId);
}
