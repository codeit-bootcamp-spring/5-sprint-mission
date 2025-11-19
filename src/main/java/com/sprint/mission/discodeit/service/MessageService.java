package com.sprint.mission.discodeit.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateCommand;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.PageResponse;

public interface MessageService {

	MessageDto create(MessageCreateCommand messageCreateCommand);

	MessageDto findById(UUID messageId);

	PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable);

	MessageDto update(UUID messageId, MessageUpdateRequest messageUpdateRequest);

	void delete(UUID messageId);

	boolean isOwner(UUID messageId, UUID userId);
}
