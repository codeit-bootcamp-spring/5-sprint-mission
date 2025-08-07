package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus userStatus);
    Optional<UserStatus> findById(UUID id);

    boolean deleteByUserId(UUID userId);

    List<UserStatus> findAll();
    Optional<UserStatus> findByUserId(UUID userId);
    boolean existsById(UUID id);
    boolean existsByUserId(UUID userId);
    boolean deleteById(UUID id);
}
