package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(ChannelType type, String name);
    Channel find(UUID channelId);
    List<Channel> findAll();
    Channel update(UUID channelId,ChannelType type, String name);
    Channel delete(UUID channelId);
}
