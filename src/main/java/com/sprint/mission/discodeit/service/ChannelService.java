package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel create(Channel channel);

    Channel updateName(UUID id, String name);

    Channel updateDescription(UUID id, String description);

    Channel updateChannelType(UUID id, String channelType);

    Channel delete(UUID id);

    void deleteAll();

    List<Channel> searchByName(String name);

    Channel searchById(UUID id);

    List<Channel> searchAll();
}
