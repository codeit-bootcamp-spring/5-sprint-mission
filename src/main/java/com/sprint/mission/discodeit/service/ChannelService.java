package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel save(Channel channelDto);

    Channel findById(UUID id);

    List<Channel> findAll();

    Channel update(UUID id, Channel channelDto);

    void delete(UUID id);

    void deleteAll();
}
