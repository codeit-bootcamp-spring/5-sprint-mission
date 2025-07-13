package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.enums.channelEntity.ChannelCategory;

import java.util.UUID;

public interface ChannelService extends Service<Channel> {
    boolean createChannel(Channel channel);

    void updateName(UUID channelId, String name);

    void updateGroupName(UUID channelId, String groupName);

    void updateChannelCategory(UUID channelId, ChannelCategory category);

    void updatePublic(UUID channelId, boolean isPublic);

    void addJoinedUser(UUID channelId, UUID userId);

    void removeJoinedUser(UUID channelId, UUID userId);

    void clearJoinedUsers(UUID channelId);
}
