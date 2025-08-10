package com.sprint.mission.discodeit.domain.vo.guild;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record GuildPermissionsId(UUID guildId, UUID userId) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public GuildPermissionsId {
        if (guildId == null || userId == null)
            throw new IllegalArgumentException("Guild id and User id must not be null");
    }
}
