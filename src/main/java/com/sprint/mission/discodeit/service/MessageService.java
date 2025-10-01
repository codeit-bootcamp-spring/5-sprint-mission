package com.sprint.mission.discodeit.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.sprint.mission.discodeit.domain.dto.CreateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.message.MessageDto;

public interface MessageService {
	MessageDto create(CreateMessageDTO dto);

	void delete(UUID id);

	MessageDto update(UpdateMessageDTO dto);

	Page<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable);

	Slice<MessageDto> findAllCursorByChannelId(UUID channelId, Instant cursor, Pageable pageable);

}
