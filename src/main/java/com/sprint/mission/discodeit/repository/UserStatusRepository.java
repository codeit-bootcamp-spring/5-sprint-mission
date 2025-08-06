package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    Optional<UserStatus> save(UserStatus userStatus);
    Optional<UserStatus> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
    void deleteAll();

    List<UserStatus> findAll();

}
