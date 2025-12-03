package com.sprint.mission.discodeit.event.auth;

import com.sprint.mission.discodeit.entity.AuthAuditEventType;

import java.util.UUID;

public record AuthAuditEvent(
    AuthAuditEventType eventType,
    UUID userId,
    String username,
    String ipAddress,
    String userAgent,
    String details
) {
}
