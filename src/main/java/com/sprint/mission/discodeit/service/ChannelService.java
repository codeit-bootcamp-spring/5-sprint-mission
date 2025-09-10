package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto create(PublicChannelCreateRequest request);
    ChannelDto create(PrivateChannelCreateRequest request);
    ChannelDto find(UUID id);
    List<ChannelDto> findAllByUserId(UUID userId);
    ChannelDto update(UUID id, PublicChannelUpdateRequest request);
    void delete(UUID id);
}
