package com.sprint.mission.discodeit.serviceprod;

import com.sprint.mission.discodeit.domain.entityprod.ProdChannel;
import com.sprint.mission.discodeit.domain.enums.channel.ChannelType;

import java.util.UUID;

public interface ProdChannelService {

    ProdChannel create(UUID guildId, String name, ChannelType type);

    void updateName(UUID channelId, String name);

    void updateType(UUID channelId, ChannelType type);

    void updatePublic(UUID channelId, boolean isPublic);

    void addJoinedUser(UUID channelId, UUID userId);

    void removeJoinedUser(UUID channelId, UUID userId);
}
