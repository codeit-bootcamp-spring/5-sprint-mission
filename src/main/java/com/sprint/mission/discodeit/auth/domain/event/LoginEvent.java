package com.sprint.mission.discodeit.auth.domain.event;

import java.util.UUID;

public record LoginEvent(
    UUID userId,
    String username,
    String ipAddress,
    String userAgent,
    long duration
) {
    public static final String TOPIC = "discodeit.auth.login.success";
}
