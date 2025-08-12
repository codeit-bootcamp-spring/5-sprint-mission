package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import javax.swing.event.ChangeEvent;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponseDto create(Object request);
    ChannelResponseDto find(UUID channelId);
    List<ChannelResponseDto> findAllByUserId(UUID userId);
    ChannelResponseDto update(ChannelUpdateRequest request);
    void delete(UUID channelId);
}
