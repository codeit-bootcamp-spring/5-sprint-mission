package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.data.ChannelDto;
import com.codeit.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelDto create(PublicChannelCreateRequest request);

    ChannelDto create(PrivateChannelCreateRequest request);

    ChannelDto find(UUID channelId);

    List<ChannelDto> findAllByUserId(UUID userId);

    ChannelDto update(UUID channelId, PublicChannelUpdateRequest request);

    void delete(UUID channelId);
}