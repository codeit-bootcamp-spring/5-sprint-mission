package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel create(String name, String description);

    Channel createPrivate(List<UUID> participantIds);

    Channel update(UUID channelId, String name, String description);

    List<Channel> findAll();

    Channel findById(UUID id);

    Channel join(UUID userId, UUID channelId);

    List<ChannelDto> findByUser(UUID userId);

    List<Channel> findByName(String name);

    boolean delete(UUID id);
}
