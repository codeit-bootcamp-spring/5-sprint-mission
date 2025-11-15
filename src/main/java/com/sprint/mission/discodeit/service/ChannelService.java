package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;

public interface ChannelService {

	ChannelDto create(PublicChannelCreateRequest publicChannelCreateRequest);

	ChannelDto create(PrivateChannelCreateRequest privateChannelCreateRequest);

	ChannelDto findById(UUID channelId);

	List<ChannelDto> findAllByUserId(UUID userId);

	ChannelDto update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest);

	void delete(UUID channelId);
}
