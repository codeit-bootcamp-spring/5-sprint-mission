package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class JcfUserStatusRepository extends AbstractJcfRepository<UserStatus> implements
    UserStatusRepository {

  public JcfUserStatusRepository() {
    super(UserStatus.class);
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    return data.values().stream()
        .filter(us -> us.isNotDeleted() && userId.equals(us.getUserId()))
        .findFirst();
  }

  @Override
  public Map<UUID, UserStatusType> findAllTypesByUserIds(Set<UUID> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return Map.of();
    }
    return data.values().stream()
        .filter(us -> us.isNotDeleted() && userIds.contains(us.getUserId()))
        .collect(Collectors.toMap(
            UserStatus::getUserId,
            UserStatus::getType
        ));
  }

  @Override
  public UserStatus getOrThrowByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    return findByUserId(userId).orElseThrow(
        () -> new NotFoundException("UserStatus with userId %s not found".formatted(userId)));
  }

  @Override
  public boolean existsByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    return findByUserId(userId).isPresent();
  }

  @Override
  public boolean softDeleteByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    return findByUserId(userId).map(us -> softDeleteById(us.getId())).orElse(false);
  }

  @Override
  public boolean hardDeleteByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    return findByUserId(userId).map(us -> hardDeleteById(us.getId())).orElse(false);
  }
}
