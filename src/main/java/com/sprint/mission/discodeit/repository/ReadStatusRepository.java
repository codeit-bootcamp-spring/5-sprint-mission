package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    Optional<ReadStatus> findById(UUID readStatusId);
    List<ReadStatus> findAllByUserId(UUID userId);
    Optional<ReadStatus> save(ReadStatus readStatus);
    void deleteAll();
    List<UUID> findUsersIdByChannelId(UUID channelId);
    List<UUID> findChannelsIdByUserId(UUID userId);
    void deleteByChannelId(UUID channelId);
}
