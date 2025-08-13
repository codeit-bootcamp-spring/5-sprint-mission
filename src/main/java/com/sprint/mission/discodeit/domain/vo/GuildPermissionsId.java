package com.sprint.mission.discodeit.domain.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public record GuildPermissionsId(UUID guildId, UUID userId) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public GuildPermissionsId {
        Objects.requireNonNull(guildId, "guildId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
    }
}
