package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;

public interface ChannelService {
    void addChannel(Channel channel);

    void updateChannel(Channel channel);

    void deleteChannel(Channel channel);

    Channel getChannel(int i);

    List<Channel> getAllChannels();
}
