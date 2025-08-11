package com.sprint.mission.discodeit.domain.entityprod;

import com.sprint.mission.discodeit.domain.enums.Permission;
import com.sprint.mission.discodeit.domain.voprod.JpaGuildPermissionsId;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "guild_member_permissions")
public class ProdGuildPermissions {

    @EmbeddedId
    private JpaGuildPermissionsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("guildId")
    @JoinColumn(name = "guild_id", nullable = false)
    private ProdGuild guild;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private ProdUser user;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "guild_permissions_entries",
            joinColumns = {
                    @JoinColumn(name = "guild_id"),
                    @JoinColumn(name = "user_id")
            }
    )
    @Column(name = "permission", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Permission> permissions;

    public ProdGuildPermissions(ProdGuild guild, ProdUser user, Set<Permission> permissions) {
        this.guild = Objects.requireNonNull(guild, "guild must not be null");
        this.user = Objects.requireNonNull(user, "user must not be null");
        this.id = new JpaGuildPermissionsId(guild.getId(), user.getId());
        this.permissions = new HashSet<>(permissions);
    }

    public void setPermissions(Set<Permission> permissions) {
        if (permissions == null || permissions.isEmpty())
            throw new IllegalArgumentException("Permissions must not be null or empty.");
        this.permissions.clear();
        this.permissions.addAll(permissions);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    @Override
    public String toString() {
        return String.format(
                "GuildPermissions[guildId=%s, userId=%s, permissions=%s]",
                guild != null ? guild.getId() : "null",
                user != null ? user.getId() : "null",
                permissions
        );
    }
}
