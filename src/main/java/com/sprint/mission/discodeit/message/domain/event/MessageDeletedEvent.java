package com.sprint.mission.discodeit.message.domain.event;

import java.util.UUID;

public record MessageDeletedEvent(
    UUID messageId
) {
    public static final String TOPIC = "discodeit.message.deleted";
}
