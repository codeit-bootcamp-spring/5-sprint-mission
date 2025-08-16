package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends AbstractRepository<ReadStatus> {

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    List<ReadStatus> findAllByUserId(UUID userId);

    List<ReadStatus> findAllByChannelId(UUID channelId);

    List<ReadStatus> findUnreadByUserId(UUID userId);

    long countUnreadByUserId(UUID userId);

    int softDeleteAllByUserId(UUID userId);

    int softDeleteAllByChannelId(UUID channelId);
}
