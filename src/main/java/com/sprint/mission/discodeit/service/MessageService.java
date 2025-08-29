package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.CreateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateMessageDTO;
import com.sprint.mission.discodeit.domain.entity.Messages;

public interface MessageService {
	Messages create(CreateMessageDTO dto);

	void delete(UUID id);

	void deleteAll();

	void deleteAllByChannelId(UUID channelId);

	Messages update(UpdateMessageDTO dto);

	Messages read(UUID id);

	List<Messages> findAllByChannelId(UUID channelId);

	List<Messages> readAllByChannelId(UUID channelId);

	boolean isEmpty(UUID channelId);
}
