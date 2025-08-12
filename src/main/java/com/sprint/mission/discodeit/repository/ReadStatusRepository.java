package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAllByChannelId(UUID channelId);
    List<ReadStatus> findAllByUserId(UUID userId);
    List<ReadStatus> findAll();
    void delete(UUID id);
    void deleteByChannelId(UUID channelId);
}
