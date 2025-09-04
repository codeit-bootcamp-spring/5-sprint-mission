package com.sprint.mission.discodeit.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sprint.mission.discodeit.domain.dto.CreateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.message.MessageDto;

public interface MessageService {
	MessageDto create(CreateMessageDTO dto);

	void delete(UUID id);

	MessageDto update(UpdateMessageDTO dto);

	MessageDto read(UUID id);

	Page<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable);

	boolean isEmpty(UUID channelId);

}
