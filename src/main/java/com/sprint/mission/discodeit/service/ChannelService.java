package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublic(@Valid ChannelCreateRequest channelCreateRequest);

    Channel createPrivate(@Valid ChannelCreateRequest channelCreateRequest);

    ChannelFindResponse findById(UUID channelId);

    ChannelFindResponse findById(Channel channel);

    List<ChannelFindResponse> findAllByUserId(UUID userId);

    Channel update(@Valid ChannelUpdateRequest channelUpdateRequest);

    void delete(UUID channelId);
}
