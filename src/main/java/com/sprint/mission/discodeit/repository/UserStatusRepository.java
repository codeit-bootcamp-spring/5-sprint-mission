package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus status);

    Optional<UserStatus> findById(UUID id);

    Optional<UserStatus> findByUserId(UUID id);

    List<UserStatus> findAll();

    void delete(UUID id);

    void deleteAll();
}