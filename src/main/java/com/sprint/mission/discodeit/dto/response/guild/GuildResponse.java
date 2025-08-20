package com.sprint.mission.discodeit.dto.response.guild;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record GuildResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String name,
        boolean discoverable,
        UUID ownerId,
        Set<UUID> userIds,
        Set<UUID> channelIds,
        Set<UUID> bannedUserIds,
        Set<GuildPermissionsResponse> permissions
) {
}
