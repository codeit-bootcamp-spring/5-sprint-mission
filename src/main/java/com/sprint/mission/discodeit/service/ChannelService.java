package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(Channel channel);

    List<Channel> getAll();

    Channel get(UUID id);

    Channel update(UUID id, String name, String description);

    void delete(UUID id);

    void deleteAll();
}
