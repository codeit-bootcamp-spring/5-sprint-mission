package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UUID userId, Instant lastAccessAt);
    Optional<UserStatus> findByUserId(UUID userId);
    List<UserStatus> findAll();
    boolean isOnline(UUID userId);
    UserStatus update(UUID userId, Instant newLastAccessAt);
    void deleteByUserId(UUID userId);
}
