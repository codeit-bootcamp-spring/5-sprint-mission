package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;

public interface ChannelService {

	ChannelDto create(PublicChannelCreateRequest publicChannelCreateRequest);

	ChannelDto create(PrivateChannelCreateRequest privateChannelCreateRequest);

	ChannelDto findById(UUID channelId);

	List<ChannelDto> findAllByUserId(UUID userId);

	ChannelDto update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest);

	void delete(UUID channelId);
}
