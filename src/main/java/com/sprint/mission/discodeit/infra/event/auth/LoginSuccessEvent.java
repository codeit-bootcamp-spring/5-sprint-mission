package com.sprint.mission.discodeit.infra.event.auth;

import java.util.UUID;

public record LoginSuccessEvent(
    UUID userId,
    String username,
    String ipAddress,
    String userAgent
) {
}
