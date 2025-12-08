package com.sprint.mission.discodeit.message.domain.dto;

import java.util.UUID;

public record MessageDeletedEvent(UUID messageId) {
    public static final String TOPIC = "discodeit.message.deleted";
}
