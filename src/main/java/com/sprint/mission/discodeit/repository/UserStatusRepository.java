package com.sprint.mission.discodeit.repository; // 레포지토리 패키지 선언

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.Optional;
import java.util.UUID;

/**
 * UserStatus 전용 레포지토리 인터페이스
 * - 사용자의 마지막 접속 시각 관리 및 온라인 여부 판단 로직에 필요한 조회 제공
 */
public interface UserStatusRepository { // UserStatus 저장소 계약 정의 시작

    UserStatus save(UserStatus entity); // UserStatus 저장(신규/갱신)

    Optional<UserStatus> findById(UUID id); // 식별자로 단건 조회

    Optional<UserStatus> findByUserId(UUID userId); // 사용자 기준 단건 조회(한 사용자당 1개 가정)

    void deleteById(UUID id); // 식별자로 삭제

    boolean existsById(UUID id);

}
