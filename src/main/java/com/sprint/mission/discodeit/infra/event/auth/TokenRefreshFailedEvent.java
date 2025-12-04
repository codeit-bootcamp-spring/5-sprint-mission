package com.sprint.mission.discodeit.infra.event.auth;

public record TokenRefreshFailedEvent(
    String username,
    String reason
) {
}
