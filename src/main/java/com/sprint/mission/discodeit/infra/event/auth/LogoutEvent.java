package com.sprint.mission.discodeit.infra.event.auth;

import java.util.UUID;

public record LogoutEvent(
    UUID userId,
    String username,
    String ipAddress,
    String userAgent
) {
}
