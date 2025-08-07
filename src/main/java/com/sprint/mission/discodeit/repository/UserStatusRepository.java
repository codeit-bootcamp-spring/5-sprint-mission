package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    Optional<UserStatus> findByUserId(UUID userId);
    UserStatus save(UserStatus status);
    void deleteByUserId(UUID userId);
}