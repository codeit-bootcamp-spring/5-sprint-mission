package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String channelname);
    Channel getChannel(UUID channelId);
    List<Channel> getAllChannels();
    Channel updateChannel(UUID channelId, String channelname);
    void deleteChannel(UUID channelId);
}
