package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    Optional<ReadStatus> save(ReadStatus channel);
    void deleteAll();
    List<UUID> findUsersIdByChannelId(UUID channelId);

}
