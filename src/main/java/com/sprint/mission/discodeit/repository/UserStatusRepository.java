package com.sprint.mission.discodeit.repository; // 레포지토리 패키지 선언

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * UserStatus 전용 레포지토리 인터페이스
 * - 사용자의 마지막 접속 시각 관리 및 온라인 여부 판단 로직에 필요한 조회 제공
 */
public interface UserStatusRepository {

    UserStatus save(UserStatus userStatus);
    Optional<UserStatus> findById(UUID id);
    Optional<UserStatus> findByUserId(UUID userId);
    List<UserStatus> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
    void deleteByUserId(UUID userId);

}
