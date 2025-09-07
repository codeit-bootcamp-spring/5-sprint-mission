package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * UserStatus 전용 레포지토리
 * - 사용자 ID 기반 조회 및 삭제 메서드 선언
 */
public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {
    Optional<UserStatus> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
