package com.sprint.mission.discodeit.domain.entityprod.guild;

import com.sprint.mission.discodeit.domain.entityprod.ProdBaseEntity;
import com.sprint.mission.discodeit.domain.entityprod.ProdChannel;
import com.sprint.mission.discodeit.domain.entityprod.ProdUser;
import com.sprint.mission.discodeit.domain.enums.Permission;
import com.sprint.mission.discodeit.util.Validators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

// todo: 카테고리 (채널의 모음) 엔티티 추가

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "guilds")
public class ProdGuild extends ProdBaseEntity {

    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private boolean discoverable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private ProdUser owner;

    @ManyToMany(mappedBy = "guilds")
    private Set<ProdUser> users = new HashSet<>();

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProdGuildPermissions> permissions = new HashSet<>();

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProdChannel> channels = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
            name = "guild_bans",
            joinColumns = @JoinColumn(name = "guild_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uq_guild_bans_guild_user",
                    columnNames = {"guild_id", "user_id"}
            )
    )
    private Set<ProdUser> bans = new HashSet<>();

    private static final Set<Permission> DEFAULT_PERMISSIONS =
            Collections.unmodifiableSet(EnumSet.of(Permission.READ_MESSAGES, Permission.SEND_MESSAGES));

    public ProdGuild(String name, boolean discoverable, ProdUser owner) {
        setName(name);
        setDiscoverable(discoverable);
        addUser(owner, Set.of(Permission.ADMINISTRATOR));
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = Validators.validateGuildName(name);
    }

    public void setOwner(ProdUser jpaUser) {
        Objects.requireNonNull(jpaUser, "Owner must not be null.");
        if (isNotMember(jpaUser)) throw new IllegalArgumentException("User is not a member of this guild.");
        setPermissions(jpaUser, Set.of(Permission.ADMINISTRATOR));
        this.owner = jpaUser;
    }

    public boolean isOwner(ProdUser jpaUser) {
        return owner.equals(jpaUser);
    }

    public Set<ProdChannel> getChannels() {
        return Collections.unmodifiableSet(channels);
    }

    public void addChannel(ProdChannel jpaChannel) {
        Objects.requireNonNull(jpaChannel, "Channel must not be null.");
        channels.add(jpaChannel);
    }

    public void removeChannel(ProdChannel jpaChannel) {
        channels.remove(jpaChannel);
    }

    public Set<ProdUser> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    public void addUser(ProdUser jpaUser, Set<Permission> permissions) {
        Objects.requireNonNull(jpaUser, "User must not be null.");
        if (isBanned(jpaUser)) throw new IllegalStateException("User is banned from this guild.");
        users.add(jpaUser);
        setPermissions(jpaUser, permissions);
    }

    public void addUser(ProdUser jpaUser) {
        addUser(jpaUser, DEFAULT_PERMISSIONS);
    }

    public void removeUser(ProdUser jpaUser) {
        Objects.requireNonNull(jpaUser, "User must not be null.");
        users.remove(jpaUser);
        permissions.removeIf(p -> p.getUser().equals(jpaUser));
    }

    public Set<ProdGuildPermissions> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void setPermissions(ProdUser jpaUser, Set<Permission> permissions) {
        Objects.requireNonNull(jpaUser, "User ust not be null.");
        if (isNotMember(jpaUser)) throw new IllegalArgumentException("User is not a member of this guild.");

        this.permissions.removeIf(p -> p.getUser().equals(jpaUser));

        if (permissions == null || permissions.isEmpty())
            this.permissions.add(new ProdGuildPermissions(this, jpaUser, DEFAULT_PERMISSIONS));
        else
            this.permissions.add(new ProdGuildPermissions(this, jpaUser, permissions));
    }

    public Set<ProdUser> getBans() {
        return Collections.unmodifiableSet(bans);
    }

    public void addBan(ProdUser jpaUser) {
        Objects.requireNonNull(jpaUser, "User must not be null.");
        bans.add(jpaUser);
    }

    public void removeBan(ProdUser jpaUser) {
        Objects.requireNonNull(jpaUser, "User must not be null.");
        bans.remove(jpaUser);
    }

    public boolean isNotMember(ProdUser jpaUser) {
        Objects.requireNonNull(jpaUser, "User must not be null.");
        return !users.contains(jpaUser);
    }

    public boolean isBanned(ProdUser jpaUser) {
        Objects.requireNonNull(jpaUser, "User must not be null.");
        return bans.contains(jpaUser);
    }

    @Override
    public String toString() {
        return String.format("Guild[id=%s, name='%s', ownerId=%s, users=%d, bans=%d]",
                getId(), name, owner != null ? owner.getId() : "null", users.size(), bans.size());
    }
}
