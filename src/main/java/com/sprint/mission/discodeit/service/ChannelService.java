package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
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

    List<Channel> findByUser(UUID userId);

    List<Channel> findByName(String name);

    boolean delete(UUID id);
}
