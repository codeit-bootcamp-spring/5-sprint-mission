package com.sprint.mission.discodeit.domain.entity.guild;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class GuildPermissionsId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "guild_id", length = 36, updatable = false, nullable = false)
    private UUID guildId;

    @Column(name = "user_id", length = 36, updatable = false, nullable = false)
    private UUID userId;

    public GuildPermissionsId(UUID guildId, UUID userId) {
        this.guildId = Objects.requireNonNull(guildId, "Guild id must not be null.");
        this.userId = Objects.requireNonNull(userId, "User id must not be null.");
    }
}
