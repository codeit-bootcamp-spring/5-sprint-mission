package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.ChannelDetail;
import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelResult;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelResult;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelResult;
import com.sprint.mission.discodeit.domain.entity.User;

public interface ChannelService {
	public CreatePublicChannelResult createPublic(CreatePublicChannelDTO dto);

	public CreatePrivateChannelResult createPrivate(CreatePrivateChannelDTO dto);

	public List<ChannelDetail> readAllByUserId(UUID userId);

	public boolean delete(UUID id);

	public UpdateChannelResult update(UpdateChannelDTO dto);

	boolean isEmpty(UUID id);

	void deleteAll();

	public List<User> getChannelParticipants(UUID id);
}
