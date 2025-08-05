package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.enums.channel.ChannelType;

import java.util.UUID;

public interface ChannelRepository extends BaseRepository<Channel> {
    void updateName(UUID channelId, String name);

    void updateType(UUID channelId, ChannelType type);

    void updatePublic(UUID channelId, boolean isPublic);
}
