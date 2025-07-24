package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(Channel channel);

    Channel create(ChannelType type, String name, String description, UUID adminUserId);

    List<Channel> getAll();

    Channel get(UUID id);

    Channel update(UUID id, String name, String description);

    void delete(UUID id);

    void deleteAll();
}
