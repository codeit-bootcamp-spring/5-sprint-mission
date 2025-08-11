package com.sprint.mission.discodeit.domain.entity;

import com.sprint.mission.discodeit.domain.enums.Permission;
import com.sprint.mission.discodeit.domain.vo.GuildPermissionsId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
public class GuildPermissions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final GuildPermissionsId id;

    private final Set<Permission> permissions;

    public GuildPermissions(UUID guildId, UUID userId, Set<Permission> permissions) {
        this.id = new GuildPermissionsId(
                Objects.requireNonNull(guildId),
                Objects.requireNonNull(userId)
        );
        if (permissions == null || permissions.isEmpty())
            throw new IllegalArgumentException("Permissions must not be null or empty.");
        if (permissions.contains(null))
            throw new NullPointerException("Permission must not be null.");
        this.permissions = EnumSet.copyOf(permissions);
    }


    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public UUID getGuildId() {
        return id.guildId();
    }

    public UUID getUserId() {
        return id.userId();
    }

    public void setPermissions(Set<Permission> permissions) {
        if (permissions == null || permissions.isEmpty())
            throw new IllegalArgumentException("Permissions must not be null or empty.");
        if (permissions.contains(null))
            throw new NullPointerException("Permission must not be null.");

        Set<Permission> copy = EnumSet.copyOf(permissions);
        if (this.permissions.equals(copy)) return;

        this.permissions.clear();
        this.permissions.addAll(copy);
    }

    public boolean hasPermission(Permission permission) {
        Objects.requireNonNull(permission, "permission must not be null");
        return permissions.contains(permission);
    }

    @Override
    public String toString() {
        return String.format("GuildPermissions[guildId=%s, userId=%s, permissions=%s]",
                getGuildId(), getUserId(), permissions
        );
    }
}
