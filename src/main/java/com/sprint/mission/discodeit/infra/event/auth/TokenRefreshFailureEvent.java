package com.sprint.mission.discodeit.infra.event.auth;

import java.util.UUID;

public record TokenRefreshFailureEvent(
    UUID userId,
    String username,
    String ipAddress,
    String userAgent,
    String reason
) {
}
