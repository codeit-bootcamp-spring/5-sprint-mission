package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record GuildCreateRequest(
        String name,
        boolean discoverable,
        UUID ownerId
) {
}
