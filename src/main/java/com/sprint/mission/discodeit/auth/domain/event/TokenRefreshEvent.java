package com.sprint.mission.discodeit.auth.domain.event;

import java.util.UUID;

public record TokenRefreshEvent(
    UUID userId,
    String username,
    String ipAddress,
    String userAgent
) {
    public static final String TOPIC = "discodeit.auth.token.refresh.success";
}
