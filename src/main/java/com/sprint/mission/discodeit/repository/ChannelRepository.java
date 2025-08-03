package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);

    Optional<Channel> findById(UUID id);

    List<Channel> findAll();

    long count();

    boolean delete(UUID id);

    boolean existsById(UUID id);

    boolean update(UUID channelUUID, String channelname, String description);
}
