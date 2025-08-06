package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.UUID;

public interface ReadStatusRepository {

    void save(ReadStatus readStatus);
    ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId);
}
