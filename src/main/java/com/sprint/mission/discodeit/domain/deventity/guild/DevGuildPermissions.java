package com.sprint.mission.discodeit.domain.deventity.guild;

import com.sprint.mission.discodeit.domain.enums.Permission;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
public class DevGuildPermissions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final DevGuildPermissionsId id;

    private final Set<Permission> permissions;

    public DevGuildPermissions(UUID guildId, UUID userId, Set<Permission> permissions) {
        this.id = new DevGuildPermissionsId(
                Objects.requireNonNull(guildId),
                Objects.requireNonNull(userId)
        );
        if (permissions == null || permissions.isEmpty())
            throw new IllegalArgumentException("Permissions must not be null or empty.");
        this.permissions = EnumSet.copyOf(permissions);
    }

    public UUID getGuild() {
        return id.guildId();
    }

    public UUID getUser() {
        return id.userId();
    }

    public void setPermissions(Set<Permission> permissions) {
        if (permissions == null || permissions.isEmpty())
            throw new IllegalArgumentException("Permissions must not be null or empty.");
        this.permissions.clear();
        this.permissions.addAll(EnumSet.copyOf(permissions));
    }

    public boolean hasPermission(Permission permission) {
        Objects.requireNonNull(permission, "permission must not be null");
        return permissions.contains(permission);
    }

    @Override
    public String toString() {
        return String.format("DevGuildPermissions[guildId=%s, userId=%s, permissions=%s]",
                getGuild(), getUser(), permissions
        );
    }
}
