package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.channelEntity.ChannelCategory;

import java.util.List;
import java.util.UUID;

public interface ChannelService extends Service {
    boolean createChannel(Channel channel);

    Channel findById(UUID id);

    List<Channel> findAll();

    void updateName(Channel channel, String name);

    void updateGroupName(Channel channel, String groupName);

    void updateChannelCategory(Channel channel, ChannelCategory category);

    void updateIsPublic(Channel channel, boolean isPublic);

    void updateAllowedUsers(Channel channel, List<User> allowedUsers);

    void deleteById(UUID id);

}
