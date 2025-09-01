package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sprint.mission.discodeit.domain.dto.CreateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateMessageDTO;
import com.sprint.mission.discodeit.domain.entity.Message;

public interface MessageService {
	Message create(CreateMessageDTO dto);

	void delete(UUID id);

	Message update(UpdateMessageDTO dto);

	Message read(UUID id);

	Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);

	boolean isEmpty(UUID channelId);

	List<UUID> findAttachmentsIds(UUID messageId);
}
