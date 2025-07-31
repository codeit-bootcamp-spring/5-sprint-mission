package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {

    // 사용자 ID 기준 조회
    boolean findByUserId(UUID userId);

    // 전체 상태 조회
    List<UserStatus> findAll();

    // 상태 저장(갱신)
    void save(UserStatus userStatus);
}
