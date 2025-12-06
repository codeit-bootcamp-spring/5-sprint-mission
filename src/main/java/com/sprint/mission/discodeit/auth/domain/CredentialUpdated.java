package com.sprint.mission.discodeit.auth.domain;

import java.util.UUID;

public record CredentialUpdated(
    UUID userId,
    String username,
    String ipAddress,
    String userAgent
) {
    public static final String TOPIC = "discodeit.auth.credential.updated";
}
