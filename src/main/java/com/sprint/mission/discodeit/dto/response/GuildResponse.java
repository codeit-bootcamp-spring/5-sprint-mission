package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.domain.entity.GuildPermissions;

import java.util.Set;
import java.util.UUID;

public record GuildResponse(
        UUID id,
        String name,
        boolean discoverable,
        UUID ownerId,
        Set<UUID> userIds,
        Set<GuildPermissions> permissions,
        Set<UUID> channelIds,
        Set<UUID> bannedUserIds
) {
}
