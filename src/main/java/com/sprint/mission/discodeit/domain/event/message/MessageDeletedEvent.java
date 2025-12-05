package com.sprint.mission.discodeit.domain.event.message;

import java.util.UUID;

public record MessageDeletedEvent(UUID messageId) {
}
