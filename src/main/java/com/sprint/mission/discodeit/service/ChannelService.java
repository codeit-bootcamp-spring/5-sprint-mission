package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  Channel createPublic(@Valid PublicChannelCreateRequest publicChannelCreateRequest);

  Channel createPrivate(@Valid PrivateChannelCreateRequest privateChannelCreateRequest);

  ChannelFindResponse findById(UUID channelId);

  ChannelFindResponse findById(Channel channel);

  List<ChannelFindResponse> findAllByUserId(UUID userId);

  Channel update(@Valid PublicChannelUpdateRequest publicChannelUpdateRequest);

  void delete(UUID channelId);
}
