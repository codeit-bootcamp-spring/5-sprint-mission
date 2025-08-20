package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.UserStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends AbstractRepository<UserStatus> {

    Optional<UserStatus> findByUserId(UUID userId);

    List<UserStatus> findAllByUserIds(Collection<UUID> userIds);

    UserStatus getOrThrowByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    boolean softDeleteByUserId(UUID userId);

    boolean hardDeleteByUserId(UUID userId);
}
