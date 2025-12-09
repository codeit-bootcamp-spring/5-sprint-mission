package com.sprint.mission.discodeit.auth.domain.event;

import com.sprint.mission.discodeit.user.domain.Role;

import java.util.UUID;

public record RoleUpdatedEvent(
    UUID userId,
    String username,
    Role oldRole,
    Role newRole
) {
    public static final String TOPIC = "discodeit.auth.role.updated";
}
