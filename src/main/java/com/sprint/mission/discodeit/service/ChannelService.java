package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.CreateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    UUID createPublicChannel(CreateChannelRequest request);
    UUID createPrivateChannel(PrivateChannelRequest request);
    Optional<ChannelResponse> findById(UUID channelId); // ✅ 대표 조회 메서드
    List<ChannelResponse> findAllByUserId(UUID userId);
    boolean update(ChannelUpdateRequest request);
    boolean delete(UUID channelId);
}
