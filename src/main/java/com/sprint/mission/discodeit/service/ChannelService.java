package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel create(ChannelCreateDto dto);

    Channel createPrivate(PrivateChannelCreateDto dto);

    List<Channel> findAll();

    Channel findById(UUID id);

    ChannelResponse update(ChannelUpdateRequest req);

    List<Channel> findByName(String name);

    boolean delete(UUID id);
}
