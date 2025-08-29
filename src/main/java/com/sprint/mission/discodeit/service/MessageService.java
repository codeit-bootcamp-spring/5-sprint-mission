package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.CreateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateMessageDTO;
import com.sprint.mission.discodeit.domain.entity.Message;

public interface MessageService {
	Message create(CreateMessageDTO dto);

	void delete(UUID id);

	void deleteAll();

	void deleteAllByChannelId(UUID channelId);

	Message update(UpdateMessageDTO dto);

	Message read(UUID id);

	List<Message> findAllByChannelId(UUID channelId);

	List<Message> readAllByChannelId(UUID channelId);

	boolean isEmpty(UUID channelId);
}
