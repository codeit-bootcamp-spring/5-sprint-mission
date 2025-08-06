package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel addChannel(String channelName, UUID ownerUserId);
    Channel getChannelById(UUID channelId);
    List<Channel> getAllChannel();
    Channel updateChannel(UUID channelId, String channelName);
    void deleteChannel(UUID channelId);
    void deleteAllChannel();
}

