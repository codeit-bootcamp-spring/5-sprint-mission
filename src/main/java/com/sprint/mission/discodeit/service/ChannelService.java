package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.channel.ChannelDto;

public interface ChannelService {
	public ChannelDto createPublic(CreatePublicChannelDTO dto);

	public ChannelDto createPrivate(CreatePrivateChannelDTO dto);

	public List<ChannelDto> readAllByUserId(UUID userId);

	public boolean delete(UUID id);

	public ChannelDto update(UpdateChannelDTO dto);

	boolean isEmpty(UUID id);

}
