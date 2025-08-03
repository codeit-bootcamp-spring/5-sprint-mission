package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channelDto);

    Optional<Channel> findById(UUID id);

    List<Channel> findAll();

    Channel update(UUID id, Channel channelDto);

    void delete(UUID id);
}
