package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserStatusRepository extends AbstractRepository<UserStatus> {

  Optional<UserStatus> findByUserId(UUID userId);

  Map<UUID, UserStatusType> findAllTypesByUserIds(Set<UUID> userIds);

  UserStatus getOrThrowByUserId(UUID userId);

  boolean existsByUserId(UUID userId);

  boolean deleteByUserId(UUID userId);

  boolean hardDeleteByUserId(UUID userId);
}
