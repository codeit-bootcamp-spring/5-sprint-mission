package com.sprint.mission.discodeit.repository.impl.file;

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class FileUserStatusRepository extends AbstractFileRepository<UserStatus> implements
    UserStatusRepository {

  public FileUserStatusRepository() {
    super(UserStatus.class);
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");

    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream)
          .filter(us -> us.isNotDeleted() && userId.equals(us.getUserId()))
          .findFirst();
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }

  @Override
  public Map<UUID, UserStatusType> findAllTypesByUserIds(Set<UUID> userIds) {
    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream)
          .filter(UserStatus::isNotDeleted)
          .filter(us -> userIds.contains(us.getUserId()))
          .collect(Collectors.toMap(
              UserStatus::getUserId,
              UserStatus::getType,
              (left, right) -> right
          ));
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }

  @Override
  public boolean deleteByUserId(UUID userId) {
    return findByUserId(userId).map(us -> delete(us.getId())).orElse(false);
  }

  @Override
  public boolean hardDeleteByUserId(UUID userId) {
    return findByUserId(userId).map(us -> hardDelete(us.getId())).orElse(false);
  }
}
