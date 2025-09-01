package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelDTO;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.User;

public interface ChannelService {
	public Channel createPublic(CreatePublicChannelDTO dto);

	public Channel createPrivate(CreatePrivateChannelDTO dto);

	public List<Channel> readAllByUserId(UUID userId);

	public boolean delete(UUID id);

	public Channel update(UpdateChannelDTO dto);

	boolean isEmpty(UUID id);

	void deleteAll();

	public List<User> getChannelParticipants(UUID id);
}
