package com.sprint.mission.discodeit.infra.event.message;

import java.util.UUID;

public record MessageDeletedEvent(UUID messageId) {
}
