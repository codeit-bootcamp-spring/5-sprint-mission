package com.sprint.mission.discodeit.domain.auth.domain.event;

public record LoginFailureEvent(
    String username,
    String ipAddress,
    String userAgent,
    String reason,
    long duration
) {
    public static final String TOPIC = "discodeit.auth.login.failure";
}
