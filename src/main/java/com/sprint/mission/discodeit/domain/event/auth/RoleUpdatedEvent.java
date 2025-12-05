package com.sprint.mission.discodeit.domain.event.auth;

import com.sprint.mission.discodeit.domain.entity.Role;

import java.util.UUID;

public record RoleUpdatedEvent(
    UUID userId,
    String username,
    Role oldRole,
    Role newRole
) {
}
