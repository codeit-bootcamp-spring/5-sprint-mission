package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(PublicChannelCreateRequest request);
    Channel create(PrivateChannelCreateRequest request);
    ChannelDto find(UUID id);
    List<ChannelDto> findAllByUserId(UUID userId);
    Channel update(UUID id, PublicChannelUpdateRequest request);
    void delete(UUID id);
}
