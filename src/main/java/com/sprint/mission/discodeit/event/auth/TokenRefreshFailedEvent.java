package com.sprint.mission.discodeit.event.auth;

public record TokenRefreshFailedEvent(
    String username,
    String reason
) {
}
