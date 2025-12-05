package com.sprint.mission.discodeit.domain.event.user;

import java.util.UUID;

public record UserDeletedEvent(UUID userId) {
    public static final String TOPIC = "discodeit.user.deleted";
}
