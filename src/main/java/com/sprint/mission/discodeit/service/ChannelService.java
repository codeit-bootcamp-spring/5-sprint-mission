package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    Channel createChannel(String channelName);

    Optional<Channel> findChannel(UUID channelId);

    List<Channel> findAllChannels();

    Channel updateChannel(UUID channelId, String channelName);

    Channel deleteChannel(UUID channelId);

}
