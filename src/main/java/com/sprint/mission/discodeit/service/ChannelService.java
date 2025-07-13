package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    public boolean addChannel(Channel Channel);
    public List<Channel> getChannels();
    public Channel getChannelById(UUID channelId);
    public Channel updateChannel(Channel Channel, UUID id);
    public Channel deleteChannel(UUID id);
    public void deleteAll();
}
