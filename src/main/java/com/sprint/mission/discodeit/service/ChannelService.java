package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String channelName, String channelDescription);
    Channel findById(UUID channelId);
    List<Channel> findByChannelName(String channelName);
    List<Channel> findAllChannels();
    Channel updateById(UUID channelId, String channelName, String channelDescription);
    boolean deleteById(UUID channelId);
}
