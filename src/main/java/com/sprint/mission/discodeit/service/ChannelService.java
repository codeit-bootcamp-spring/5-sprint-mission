package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(Channel channel);
    Channel getChannelById(UUID id);
    List<Channel> getAllChannels();
    Channel updateChannel(Channel channel);
    void deleteChannel(UUID uuid);
}
