package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.UserStatus;

import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends BaseRepository<UserStatus> {

    Optional<UserStatus> findByUserId(UUID userId);

    UserStatus getOrThrowByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    boolean deleteByUserId(UUID userId);

    boolean hardDeleteByUserId(UUID userId);
}
