package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus userStatusId);

    Optional<UserStatus> findById(UUID userStatusId);

    List<UserStatus> findAll();

    boolean existsById(UUID userStatusId);

    boolean delete(UUID userStatusId);
}
