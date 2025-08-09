package com.sprint.mission.discodeit.domain.entity.guild;

import com.sprint.mission.discodeit.domain.entity.BaseEntity;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.User;
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
public class Guild extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private boolean discoverable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToMany(mappedBy = "guilds")
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GuildPermissions> permissions = new HashSet<>();

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Channel> channels = new LinkedHashSet<>();

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
    private Set<User> bans = new HashSet<>();

    private static final Set<Permission> DEFAULT_PERMISSIONS =
            Collections.unmodifiableSet(EnumSet.of(Permission.READ_MESSAGES, Permission.SEND_MESSAGES));

    public Guild(String name, boolean discoverable, User owner) {
        setName(name);
        setDiscoverable(discoverable);
        addUser(owner, Set.of(Permission.ADMINISTRATOR));
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = Validators.validateGuildName(name);
    }

    public void setOwner(User user) {
        Objects.requireNonNull(user, "Owner must not be null.");
        if (isNotMember(user)) throw new IllegalArgumentException("User is not a member of this guild.");
        setPermissions(user, Set.of(Permission.ADMINISTRATOR));
        this.owner = user;
    }

    public boolean isOwner(User user) {
        return owner.equals(user);
    }

    public Set<Channel> getChannels() {
        return Collections.unmodifiableSet(channels);
    }

    public void addChannel(Channel channel) {
        Objects.requireNonNull(channel, "Channel must not be null.");
        channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
    }

    public Set<User> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    public void addUser(User user, Set<Permission> permissions) {
        Objects.requireNonNull(user, "User must not be null.");
        if (isBanned(user)) throw new IllegalStateException("User is banned from this guild.");
        users.add(user);
        setPermissions(user, permissions);
    }

    public void addUser(User user) {
        addUser(user, DEFAULT_PERMISSIONS);
    }

    public void removeUser(User user) {
        Objects.requireNonNull(user, "User must not be null.");
        users.remove(user);
        permissions.removeIf(p -> p.getUser().equals(user));
    }

    public Set<GuildPermissions> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void setPermissions(User user, Set<Permission> permissions) {
        Objects.requireNonNull(user, "User ust not be null.");
        if (isNotMember(user)) throw new IllegalArgumentException("User is not a member of this guild.");

        this.permissions.removeIf(p -> p.getUser().equals(user));

        if (permissions == null || permissions.isEmpty())
            this.permissions.add(new GuildPermissions(this, user, DEFAULT_PERMISSIONS));
        else
            this.permissions.add(new GuildPermissions(this, user, permissions));
    }

    public Set<User> getBans() {
        return Collections.unmodifiableSet(bans);
    }

    public void addBan(User user) {
        Objects.requireNonNull(user, "User must not be null.");
        bans.add(user);
    }

    public void removeBan(User user) {
        Objects.requireNonNull(user, "User must not be null.");
        bans.remove(user);
    }

    public boolean isNotMember(User user) {
        Objects.requireNonNull(user, "User must not be null.");
        return !users.contains(user);
    }

    public boolean isBanned(User user) {
        Objects.requireNonNull(user, "User must not be null.");
        return bans.contains(user);
    }

    @Override
    public String toString() {
        return String.format("Guild[id=%s, name='%s', ownerId=%s, users=%d, bans=%d]",
                getId(), name, owner != null ? owner.getId() : "null", users.size(), bans.size());
    }
}
