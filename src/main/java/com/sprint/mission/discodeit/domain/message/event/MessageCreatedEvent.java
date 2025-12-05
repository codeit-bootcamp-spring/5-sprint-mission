package com.sprint.mission.discodeit.domain.message.event;

import java.util.UUID;

public record MessageCreatedEvent(UUID messageId) {
    public static final String TOPIC = "discodeit.message.created";
}
