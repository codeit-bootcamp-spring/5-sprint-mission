package com.sprint.mission.discodeit.repository.impl.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import com.sprint.mission.discodeit.exception.NotFoundException;
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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@Profile("dev")
public class FileUserStatusRepository extends AbstractFileRepository<UserStatus> implements
    UserStatusRepository {

  public FileUserStatusRepository(AppProperties appProperties) {
    super(UserStatus.class, appProperties.storage());
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");

    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream)
          .filter(us -> !us.isDeleted() && userId.equals(us.getUserId()))
          .findFirst();
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }

  @Override
  public Map<UUID, UserStatusType> findAllTypesByUserIds(Set<UUID> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return Map.of();
    }

    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream)
          .filter(us -> !us.isDeleted() && userIds.contains(us.getUserId()))
          .collect(Collectors.toMap(
              UserStatus::getUserId,
              UserStatus::getType
          ));
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }

  @Override
  public UserStatus getOrThrowByUserId(UUID userId) {
    return findByUserId(userId).orElseThrow(
        () -> new NotFoundException("UserStatus with userId %s not found".formatted(userId)));
  }

  @Override
  public boolean existsByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream)
          .anyMatch(us -> !us.isDeleted() && userId.equals(us.getUserId()));
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }

  @Override
  public boolean softDeleteByUserId(UUID userId) {
    return findByUserId(userId).map(us -> softDeleteById(us.getId())).orElse(false);
  }

  @Override
  public boolean hardDeleteByUserId(UUID userId) {
    return findByUserId(userId).map(us -> hardDeleteById(us.getId())).orElse(false);
  }
}
