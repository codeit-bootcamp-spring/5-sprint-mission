package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void create(Channel channel);
    Channel findById(UUID channelId);
    Channel findByName(String channelName);
    List<Channel> findAll();
    void update(UUID channelId, String channelName);
    void delete(UUID channelId);
}
