package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto create(@Valid PublicChannelCreateRequest publicChannelCreateRequest);

  ChannelDto create(@Valid PrivateChannelCreateRequest privateChannelCreateRequest);

  ChannelDto findById(UUID channelId);

  List<ChannelDto> findAllByUserId(UUID userId);

  ChannelDto update(UUID channelId, @Valid PublicChannelUpdateRequest publicChannelUpdateRequest);

  void delete(UUID channelId);
}
