package com.sprint.mission.discodeit.service.impl;


import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.enums.channel.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Profile("prod")
public class JpaChannelService implements ChannelService {
    @Override
    public Channel create(UUID guildId, String name, ChannelType type) {
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
