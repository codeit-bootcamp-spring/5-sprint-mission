package com.sprint.mission.discodeit.serviceprod.impl;


import com.sprint.mission.discodeit.domain.entityprod.ProdChannel;
import com.sprint.mission.discodeit.domain.enums.channel.ChannelType;
import com.sprint.mission.discodeit.serviceprod.ProdChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Profile("prod")
public class JpaChannelService implements ProdChannelService {
    @Override
    public ProdChannel create(UUID guildId, String name, ChannelType type) {
        return null;
    }

    @Override
    public void updateName(UUID channelId, String name) {

    }

    @Override
    public void updateType(UUID channelId, ChannelType type) {

    }

    @Override
    public void updatePublic(UUID channelId, boolean isPublic) {

    }

    @Override
    public void addJoinedUser(UUID channelId, UUID userId) {

    }

    @Override
    public void removeJoinedUser(UUID channelId, UUID userId) {

    }
}
