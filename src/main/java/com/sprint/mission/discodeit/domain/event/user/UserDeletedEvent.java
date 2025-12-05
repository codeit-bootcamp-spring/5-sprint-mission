package com.sprint.mission.discodeit.domain.event.user;

import java.util.UUID;

public record UserDeletedEvent(
    UUID userId
) {
}
