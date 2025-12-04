package com.sprint.mission.discodeit.infra.event.auth;

import com.sprint.mission.discodeit.domain.entity.AuthAuditEventType;

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
