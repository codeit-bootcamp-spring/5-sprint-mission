package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.channel.ChannelDto;

public interface ChannelService {
	ChannelDto createPublic(CreatePublicChannelDTO dto);

	ChannelDto createPrivate(CreatePrivateChannelDTO dto);

	List<ChannelDto> readAllByUserId(UUID userId);

	boolean delete(UUID id);

	ChannelDto update(UpdateChannelDTO dto);

}
