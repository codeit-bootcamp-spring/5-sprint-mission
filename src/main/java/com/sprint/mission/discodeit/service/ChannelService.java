package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void addChannel(Channel channel);
    void updateChannel(Channel channel);
    void deleteChannel(UUID id);
    Channel getChannel(UUID id);
    HashMap<UUID, Channel> getAllChannels();
}
