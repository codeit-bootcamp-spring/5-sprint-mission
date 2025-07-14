package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface channelService {

    void createChannel(Channel channel);

    Channel getChannelById(UUID channelId);

    List<Channel> getAllChannels();

    void updateChannel(UUID channelId, Channel channl);

    void updateChannelUpdatedAt(UUID ChannelId, long updatedAt);

    void deleteChannel(UUID channelId);
}
