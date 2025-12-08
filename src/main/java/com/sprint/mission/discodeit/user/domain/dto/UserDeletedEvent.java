package com.sprint.mission.discodeit.user.domain.dto;

import java.util.UUID;

public record UserDeletedEvent(UUID userId) {
    public static final String TOPIC = "discodeit.user.deleted";
}
