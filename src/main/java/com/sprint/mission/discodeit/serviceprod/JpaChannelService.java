package com.sprint.mission.discodeit.serviceprod;


import com.sprint.mission.discodeit.domain.entityprod.ProdChannel;
import com.sprint.mission.discodeit.domain.enums.channel.ChannelType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Profile("prod")
public class JpaChannelService {
    public ProdChannel create(UUID guildId, String name, ChannelType type) {
        return null;
    }

    public void updateName(UUID channelId, String name) {

    }

    public void updateType(UUID channelId, ChannelType type) {

    }

    public void updatePublic(UUID channelId, boolean isPublic) {

    }

    public void addJoinedUser(UUID channelId, UUID userId) {

    }

    public void removeJoinedUser(UUID channelId, UUID userId) {

    }
}
