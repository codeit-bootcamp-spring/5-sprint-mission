package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void addChannel(Channel Channel);
    List<Channel> getChannels();
    Channel getChannelById(UUID channelId);
    void updateChannel(Channel Channel, UUID id);
    void deleteChannel(UUID id);
    void deleteAll();
}
