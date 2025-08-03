package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelType type, String name, UUID ownerId);

    Channel find(UUID channelId);

    List<Channel> findAll();

    Channel update(UUID channelId, String name, UUID ownerId);

    boolean delete(UUID channelId);
}
