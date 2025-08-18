package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.Optional;
import java.util.UUID;

public interface UserStatusService {

    Optional<UserStatus> findByUserId(UUID userId);

    /**
     * 특정 사용자의 온라인 여부를 확인합니다.
     */
    boolean isOnline(UUID userId);

    /**
     * 특정 사용자의 마지막 접속 시각을 현재 시간으로 갱신합니다.
     */
    void updateLastAccessedAt(UUID userId);

}
