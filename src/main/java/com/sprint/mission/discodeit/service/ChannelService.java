package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String channelName, String description);
    Channel find(UUID uuid);
    List<Channel> findAll();
    Channel updateChannel(UUID uuid, String channelName, String description);
    Channel deleteChannel(UUID uuid);
}
