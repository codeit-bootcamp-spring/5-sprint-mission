package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserStatusRepository extends AbstractRepository<UserStatus> {

  Optional<UserStatus> findByUserId(UUID userId);

  Set<UserStatus> findAllByUserId(Set<UUID> userIds);

  UserStatus getOrThrowByUserId(UUID userId);

  boolean existsByUserId(UUID userId);

  boolean softDeleteByUserId(UUID userId);

  boolean hardDeleteByUserId(UUID userId);
}
