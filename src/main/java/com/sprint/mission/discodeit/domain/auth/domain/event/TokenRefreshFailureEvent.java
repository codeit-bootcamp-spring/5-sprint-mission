package com.sprint.mission.discodeit.domain.auth.domain.event;

import java.util.UUID;

public record TokenRefreshFailureEvent(
    UUID userId,
    String username,
    String ipAddress,
    String userAgent,
    String reason
) {
    public static final String TOPIC = "discodeit.auth.token.refresh.failure";
}
