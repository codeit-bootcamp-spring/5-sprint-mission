package com.sprint.mission.discodeit.service.dev;

import com.sprint.mission.discodeit.domain.entitydev.DevChannel;
import com.sprint.mission.discodeit.domain.enums.channel.ChannelType;

import java.util.UUID;

public interface DevChannelService {

    DevChannel create(UUID guildId, String name, ChannelType type);

    void updateName(UUID channelId, String name);

    void updateType(UUID channelId, ChannelType type);

    void updatePrivate(UUID channelId, boolean isPublic);

    void addJoinedUser(UUID channelId, UUID userId);

    void removeJoinedUser(UUID channelId, UUID userId);
}
