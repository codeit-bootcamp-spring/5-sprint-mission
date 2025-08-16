package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelPrivateCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelPublicCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;

import java.util.List;
import java.util.UUID;


public interface ChannelService {
    ChannelResponse createPrivateChannel(ChannelPrivateCreateRequest request);

    ChannelResponse createPublicChannel(ChannelPublicCreateRequest request);

    ChannelResponse find(UUID channelId);

    List<ChannelResponse> findAll();

    List<ChannelResponse> findAllByUserId(UUID userId);

    ChannelResponse update(ChannelUpdateRequest request);

    void delete(UUID channelId);

    /**
     * 모든 채널 데이터를 초기화합니다.
     * 테스트 환경에서 사용됩니다.
     */
    void clear();
}
