package com.sprint.mission.discodeit.domain.auth.event;

import java.util.UUID;

public record TokenRefreshSuccessEvent(
    UUID userId,
    String username,
    String ipAddress,
    String userAgent
) {
    public static final String TOPIC = "discodeit.auth.token.refresh.success";
}
