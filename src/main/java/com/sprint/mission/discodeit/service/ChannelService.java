package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse create(ChannelCreateRequest request);

    ChannelResponse createPublic(ChannelCreateRequest request);

    ChannelResponse createPrivate(PrivateChannelCreateRequest request);

    ChannelResponse findById(UUID channelId);

    List<ChannelResponse> findAll();

    List<ChannelResponse> findAllByUserId(UUID userId);

    ChannelResponse update(ChannelUpdateRequest request);

    void delete(UUID channelId);
}
