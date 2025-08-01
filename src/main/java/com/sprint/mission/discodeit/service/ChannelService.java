package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;

public interface ChannelService {
    void createChannel(String channelName, String creatorUser);
    Channel findByChannelName(String channelName);
    List<Channel> findAllChannels();
    void updateChannel(String channelName);
    void deleteChannel(String channelName);
}
