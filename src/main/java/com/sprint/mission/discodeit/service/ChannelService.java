package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.enums.channel.ChannelType;

import java.util.UUID;

public interface ChannelService {

    Channel create(UUID guildId, String name, ChannelType type);

    void updateName(UUID channelId, String name);

    void updateType(UUID channelId, ChannelType type);

    void updatePublic(UUID channelId, boolean isPublic);

    void addJoinedUser(UUID channelId, UUID userId);

    void removeJoinedUser(UUID channelId, UUID userId);
}
