package com.sprint.mission.discodeit.event.auth;

import com.sprint.mission.discodeit.entity.Role;

import java.util.UUID;

public record RoleUpdatedEvent(
    UUID userId,
    String username,
    Role oldRole,
    Role newRole
) {
}
