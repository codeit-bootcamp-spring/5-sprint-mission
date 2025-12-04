package com.sprint.mission.discodeit.infra.event.user;

import java.util.UUID;

public record UserDeletedEvent(UUID userId) {
}
