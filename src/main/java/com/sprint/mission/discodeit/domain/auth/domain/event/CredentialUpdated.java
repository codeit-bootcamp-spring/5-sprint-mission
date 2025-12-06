package com.sprint.mission.discodeit.domain.auth.domain.event;

import java.util.UUID;

public record CredentialUpdated(
    UUID userId,
    String username,
    String ipAddress,
    String userAgent
) {
    public static final String TOPIC = "discodeit.auth.credential.updated";
}
