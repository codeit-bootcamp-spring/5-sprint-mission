package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublic(ChannelCreateRequest request);

    Channel createPrivate(ChannelCreateRequest request);

    ChannelFindResponse findById(UUID channelId);

    ChannelFindResponse findById(Channel channel);

    List<ChannelFindResponse> findAllByUserId(UUID userId);

    Channel update(ChannelUpdateRequest request);

    void delete(UUID channelId);
}
