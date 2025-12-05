package com.sprint.mission.discodeit.domain.auth.event;

import com.sprint.mission.discodeit.domain.user.entity.Role;

import java.util.UUID;

public record RoleUpdatedEvent(
    UUID userId,
    String username,
    Role oldRole,
    Role newRole
) {
    public static final String TOPIC = "discodeit.auth.role.updated";
}
