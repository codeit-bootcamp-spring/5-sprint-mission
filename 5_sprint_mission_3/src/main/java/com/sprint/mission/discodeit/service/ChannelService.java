package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.PrivateChannelResponseDto;
import com.sprint.mission.discodeit.dto.response.PublicChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublicChannel(PublicChannelCreateRequest request);
    Channel createPrivateChannel(PrivateChannelCreateRequest request);

    PublicChannelResponseDto findPublicChannel(UUID channelId);
    PrivateChannelResponseDto findPrivateChannel(UUID channelId);

    List<ChannelResponse> findAllByUserId(UUID userId);

    Channel update(ChannelUpdateRequest request);
    void delete(UUID channelId);

    List<UUID> findParticipantsByChannelId(UUID channelId);
    void addParticipant(UUID channelId, UUID userId);
    void removeParticipant(UUID channelId, UUID userId);
}
