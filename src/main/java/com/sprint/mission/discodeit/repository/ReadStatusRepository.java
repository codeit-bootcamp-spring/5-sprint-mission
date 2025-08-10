package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    void deleteByChannelId(UUID channelId);
    List<ReadStatus> findByChannelId(UUID channelId);
    List<UUID> findChannelIdsByUserId(UUID userId);
    List<ReadStatus> findByUserId(UUID userId);
    List<UUID> findUserIdsByChannelId(UUID channelId);
    boolean existsById(UUID id);
    void deleteById(UUID id);
    Optional<ReadStatus> findById(UUID id);
}
