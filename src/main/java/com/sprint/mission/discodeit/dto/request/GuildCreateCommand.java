package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record GuildCreateCommand(
        String name,
        boolean discoverable,
        UUID ownerId
) {
}
