package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.CreateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateMessageDTO;
import com.sprint.mission.discodeit.domain.entity.Message;

public interface MessageService {
	Message create(CreateMessageDTO dto);

	void delete(UUID id);

	Message update(UpdateMessageDTO dto);

	Message read(UUID id);

	List<Message> findAllByChannelId(UUID channelId);

	boolean isEmpty(UUID channelId);

	List<UUID> findAttachmentsIds(UUID messageId);
}
