package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.message.MessageDto;

public record MessageCreatedEvent(
	MessageDto message
) {
}
