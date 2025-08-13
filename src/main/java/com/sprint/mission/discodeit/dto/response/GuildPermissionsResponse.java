package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.domain.enums.Permission;

import java.util.Set;
import java.util.UUID;

public record GuildPermissionsResponse(
        UUID userId,
        Set<Permission> permissions
) {
}
