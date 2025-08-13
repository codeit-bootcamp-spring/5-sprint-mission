package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.channel.ChannelResponse;
import com.codeit.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.codeit.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.codeit.mission.discodeit.dto.channel.PublicChannelCreateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPublicChannel(PublicChannelCreateRequest request);

    ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);

    ChannelResponse find(UUID channelId);

    List<ChannelResponse> findAllByUserId(UUID userId);

    ChannelResponse update(ChannelUpdateRequest request);

    void delete(UUID channelId);
}
