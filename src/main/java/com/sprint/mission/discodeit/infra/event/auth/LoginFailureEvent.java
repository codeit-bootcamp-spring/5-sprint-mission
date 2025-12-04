package com.sprint.mission.discodeit.infra.event.auth;

public record LoginFailureEvent(
    String username,
    String ipAddress,
    String userAgent,
    String reason
) {
}
