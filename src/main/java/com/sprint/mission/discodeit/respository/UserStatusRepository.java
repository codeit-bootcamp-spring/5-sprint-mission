package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

    /**
     * 사용자 상태 조회
     * @param userId 사용자 아이디
     * @return UserStatus 객체 반환
     **/
    Optional<UserStatus> findById(UUID userId);

    // 전체 상태 조회
    List<UserStatus> findAll();

    // 상태 저장(갱신)
    void save(UserStatus userStatus);
}
