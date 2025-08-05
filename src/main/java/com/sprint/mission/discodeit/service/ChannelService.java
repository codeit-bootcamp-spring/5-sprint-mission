package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(UUID userId, String channelName, ChannelType channelType, boolean nsfw);
    Channel findById(UUID channelId);
    List<Channel> findAll();
    Channel update(UUID channelId, UUID ownerId, String channelName, boolean nsfw);
    Channel deleteById(UUID channelId);
}
