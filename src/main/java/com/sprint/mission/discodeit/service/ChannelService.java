package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void createChannel(Channel channel);

    void updateChannel(Channel channel);

    void deleteChannel(Channel channel);

    Channel searchByIndex(int i);

    List<Channel> searchByName(String name);

    Channel searchById(UUID id);

    List<Channel> getAllChannels();
}
