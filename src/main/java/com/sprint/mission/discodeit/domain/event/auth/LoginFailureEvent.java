package com.sprint.mission.discodeit.domain.event.auth;

public record LoginFailureEvent(
    String username,
    String ipAddress,
    String userAgent,
    String reason,
    long duration
) {
}
