package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void add(Channel channel);
    Channel findOne(UUID channelId);
    List<Channel> findAll();
    void update(UUID channelId, Channel channel);
    void delete(UUID channelId);
    void deleteAll();
}

