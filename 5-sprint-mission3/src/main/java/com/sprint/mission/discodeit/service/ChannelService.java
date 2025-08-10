package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelFindResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublicChannel(ChannelCreateRequest request);
    Channel createPrivateChannel(ChannelCreateRequest request);
    ChannelFindResponse find(UUID channelId);
    List<ChannelFindResponse> findAll(UUID userId);
    Instant getLastReadAt(UUID channelId);
    Channel update(ChannelUpdateRequest request);
    void delete(UUID channelId);
}
