package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel addChannel(String channelName, User ownerUser);
    Channel getChannelById(UUID channelId);
    List<Channel> getAllChannel();
    Channel updateChannel(UUID channelId, String channelName);
    void deleteChannel(UUID channelId);
    void deleteAllChannel();
    //
    void addUserToChannel(UUID channelId, User user);
    void deleteUserFromChannel(UUID channelId, User user);
}

