package com.sprint.mission.discodeit.repository.impl.file;

import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class FileReadStatusRepository extends AbstractFileRepository<ReadStatus> implements
    ReadStatusRepository {

  public FileReadStatusRepository() {
    super(ReadStatus.class);
  }

  @Override
  public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
    Objects.requireNonNull(userId, "userId must not be null");
    Objects.requireNonNull(channelId, "channelId must not be null");
    return findAll().stream()
        .filter(rs -> userId.equals(rs.getUserId()) && channelId.equals(rs.getChannelId()))
        .findFirst();
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    return findAll().stream()
        .filter(rs -> userId.equals(rs.getUserId()))
        .toList();
  }

  @Override
  public List<ReadStatus> findAllByChannelId(UUID channelId) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    return findAll().stream()
        .filter(rs -> channelId.equals(rs.getChannelId()))
        .toList();
  }

  @Override
  public List<ReadStatus> findUnreadByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    // return findAll().stream()
    //         .filter(rs -> userId.equals(rs.getUserId()))
    //         .filter(rs -> !rs.isRead())
    //         .toList();
    return List.of();
  }

  @Override
  public long countUnreadByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    // return findAll().stream()
    //         .filter(rs -> userId.equals(rs.getUserId()))
    //         .filter(rs -> !rs.isRead())
    //         .count();
    return 0L;
  }

  @Override
  public void deleteAllByUserId(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    Set<UUID> ids = findAll().stream()
        .filter(rs -> userId.equals(rs.getUserId()))
        .map(ReadStatus::getId)
        .collect(Collectors.toSet());
    deleteAllByIdIn(ids);
  }

  @Override
  public void deleteAllByChannelId(UUID channelId) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    Set<UUID> ids = findAll().stream()
        .filter(rs -> channelId.equals(rs.getChannelId()))
        .map(ReadStatus::getId)
        .collect(Collectors.toSet());
    deleteAllByIdIn(ids);
  }
}
