package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Profile("test")
public class JcfReadStatusRepository extends JcfBaseRepository<ReadStatus> implements ReadStatusRepository {

    @Override
    protected String getEntityTypeName() {
        return "ReadStatus";
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
        return findAll().stream()
                .filter(rs -> userId.equals(rs.getUserId()))
                .filter(rs -> !rs.isRead())
                .toList();
    }

    @Override
    public long countUnreadByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        return findAll().stream()
                .filter(rs -> userId.equals(rs.getUserId()))
                .filter(rs -> !rs.isRead())
                .count();
    }

    @Override
    public int softDeleteAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        Set<UUID> ids = findAll().stream()
                .filter(rs -> userId.equals(rs.getUserId()))
                .map(ReadStatus::getId)
                .collect(Collectors.toSet());
        return softDeleteAllByIds(ids);
    }

    @Override
    public int softDeleteAllByChannelId(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId must not be null");
        Set<UUID> ids = findAll().stream()
                .filter(rs -> channelId.equals(rs.getChannelId()))
                .map(ReadStatus::getId)
                .collect(Collectors.toSet());
        return softDeleteAllByIds(ids);
    }
}
