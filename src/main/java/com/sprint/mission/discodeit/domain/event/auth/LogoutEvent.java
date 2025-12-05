package com.sprint.mission.discodeit.domain.event.auth;

import java.util.UUID;

public record LogoutEvent(
    UUID userId,
    String username,
    String ipAddress,
    String userAgent
) {
    public static final String TOPIC = "discodeit.auth.logout";
}
