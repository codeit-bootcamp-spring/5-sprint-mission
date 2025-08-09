package com.sprint.mission.discodeit.domain.deventity.guild;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record DevGuildPermissionsId(UUID guildId, UUID userId) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public DevGuildPermissionsId {
        if (guildId == null || userId == null)
            throw new IllegalArgumentException("Guild id and User id must not be null");
    }
}
