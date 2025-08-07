package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAllByUserId(UUID userId);
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    List<ReadStatus> findAll();

    boolean existsById(UUID id);
    boolean deleteById(UUID id);
    List<ReadStatus> findAllByChannelId(UUID channelId);

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    void deleteAllByUserId(UUID userId);

    void deleteAllByChannelId(UUID channelId);
}