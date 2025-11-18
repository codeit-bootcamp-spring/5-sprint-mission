package com.sprint.mission.discodeit.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.neutral.MessageCreateCommand;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;

public interface MessageService {

	MessageDto create(MessageCreateCommand messageCreateCommand);

	MessageDto findById(UUID messageId);

	PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable);

	MessageDto update(UUID messageId, MessageUpdateRequest messageUpdateRequest);

	void delete(UUID messageId);

	boolean isOwner(UUID messageId, UUID userId);
}
