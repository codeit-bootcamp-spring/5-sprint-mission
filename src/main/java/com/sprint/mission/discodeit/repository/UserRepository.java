package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * User 전용 레포지토리
 * - 사용자명/이메일 중복 체크, 단일 조회용 메서드 선언
 */
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
