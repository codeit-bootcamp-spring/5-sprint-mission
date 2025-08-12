package com.sprint.mission.discodeit.domain.entity;

import com.sprint.mission.discodeit.domain.enums.Permission;
import com.sprint.mission.discodeit.domain.vo.GuildPermissionsId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "guildPermissionsId", callSuper = false)
public class GuildPermissions extends BaseEntity {

    private final GuildPermissionsId guildPermissionsId;

    private final Set<Permission> permissions;

    public GuildPermissions(UUID guildId, UUID userId, Set<Permission> permissions) {
        this.guildPermissionsId = new GuildPermissionsId(
                Objects.requireNonNull(guildId, "guildId must not be null"),
                Objects.requireNonNull(userId, "userId must not be null")
        );
        this.permissions = validateAndCopy(permissions);
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public UUID getGuildId() {
        return guildPermissionsId.guildId();
    }

    public UUID getUserId() {
        return guildPermissionsId.userId();
    }

    public boolean hasPermission(Permission permission) {
        Objects.requireNonNull(permission, "permission must not be null");
        return permissions.contains(permission);
    }

    public void setPermissions(Set<Permission> newPermissions) {
        Set<Permission> copy = validateAndCopy(newPermissions);
        if (this.permissions.equals(copy)) return;
        this.permissions.clear();
        this.permissions.addAll(copy);
    }

    private static Set<Permission> validateAndCopy(Set<Permission> source) {
        if (source == null || source.isEmpty()) {
            throw new IllegalArgumentException("permissions must not be null or empty");
        }
        if (source.contains(null)) {
            throw new NullPointerException("permission element must not be null");
        }
        return EnumSet.copyOf(source);
    }

    @Override
    public String toString() {
        return "GuildPermissions[guildId=%s, guildPermissionsId=%s, userId=%s, permissions=%s]"
                .formatted(getGuildId(), getGuildPermissionsId(), getUserId(), permissions);
    }
}
