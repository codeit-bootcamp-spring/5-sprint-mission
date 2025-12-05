package com.sprint.mission.discodeit.domain.event.auth;

import java.util.UUID;

public record LoginSuccessEvent(
    UUID userId,
    String username,
    String ipAddress,
    String userAgent,
    long duration
) {
    public static final String TOPIC = "discodeit.auth.login.success";
}
