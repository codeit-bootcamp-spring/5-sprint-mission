package com.sprint.mission.discodeit.message.domain.dto;

import java.util.UUID;

public record MessageCreatedEvent(
    UUID messageId
) {
    public static final String TOPIC = "discodeit.message.created";
}
