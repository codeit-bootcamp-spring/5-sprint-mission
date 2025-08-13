package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelResponse.detail create(ChannelRequest.create dto);

    ChannelResponse.detail update(ChannelRequest.update dto);

    ChannelResponse.detail createPrivate(ChannelRequest.createPrivate dto);

    List<Channel> findAll();

    Channel findById(UUID id);

    ChannelResponse.join join(UUID userId, UUID channelId);

    List<ChannelResponse.summary> findByUser(UUID userId);

    List<Channel> findByName(String name);

    boolean delete(UUID id);
}
