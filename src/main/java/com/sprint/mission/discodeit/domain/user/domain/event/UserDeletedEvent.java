package com.sprint.mission.discodeit.domain.user.domain.event;

import java.util.UUID;

public record UserDeletedEvent(UUID userId) {
    public static final String TOPIC = "discodeit.user.deleted";
}
