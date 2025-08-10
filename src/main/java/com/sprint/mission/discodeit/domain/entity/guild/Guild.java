package com.sprint.mission.discodeit.domain.entity.guild;

import com.sprint.mission.discodeit.domain.entity.BaseEntity;
import com.sprint.mission.discodeit.domain.enums.Permission;
import com.sprint.mission.discodeit.util.Validators;
import lombok.Getter;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
public class Guild extends BaseEntity {

    private String name;

    private boolean discoverable;

    private UUID owner;

    private final Set<UUID> users = new HashSet<>();

    private final Set<GuildPermissions> permissions = new HashSet<>();

    private final Set<UUID> channels = new LinkedHashSet<>();

    private final Set<UUID> bans = new HashSet<>();

    private static final Set<Permission> DEFAULT_PERMISSIONS =
            Collections.unmodifiableSet(EnumSet.of(Permission.READ_MESSAGES, Permission.SEND_MESSAGES));

    public Guild(String name, boolean discoverable, UUID owner) {
        setName(name);
        setDiscoverable(discoverable);
        addUser(owner, Set.of(Permission.ADMINISTRATOR));
        this.owner = owner;
        touch();
    }

    public void setName(String name) {
        this.name = Validators.validateGuildName(name);
        touch();
    }

    public void setDiscoverable(boolean discoverable) {
        if (this.discoverable != discoverable) {
            this.discoverable = discoverable;
            touch();
        }
    }

    public void setOwner(UUID user) {
        Objects.requireNonNull(user, "Owner must not be null.");
        if (isBanned(user)) throw new IllegalStateException("Banned user cannot be the owner.");
        if (isNotMember(user)) throw new IllegalArgumentException("User is not a member of this guild.");

        setPermissions(user, Set.of(Permission.ADMINISTRATOR));
        if (!user.equals(this.owner)) {
            this.owner = user;
            touch();
        }
    }

    public boolean isOwner(UUID user) {
        Objects.requireNonNull(user, "User id must not be null.");
        return user.equals(owner);
    }

    public Set<UUID> getChannels() {
        return Collections.unmodifiableSet(channels);
    }

    public void addChannel(UUID channel) {
        Objects.requireNonNull(channel, "Channel must not be null.");
        if (channels.add(channel)) touch();
    }

    public void removeChannel(UUID channel) {
        if (channels.remove(channel)) touch();
    }

    public Set<UUID> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    public void addUser(UUID user, Set<Permission> permissions) {
        Objects.requireNonNull(user, "User must not be null.");
        if (isBanned(user)) throw new IllegalStateException("User is banned from this guild.");
        boolean added = users.add(user);
        setPermissions(user, permissions);
        if (added) touch();
    }

    public void addUser(UUID user) {
        addUser(user, DEFAULT_PERMISSIONS);
    }

    public void removeUser(UUID user) {
        Objects.requireNonNull(user, "User id must not be null.");
        if (isOwner(user)) throw new IllegalStateException("Cannot remove the guild owner. Transfer ownership first.");
        boolean removed = users.remove(user);
        boolean permsRemoved = this.permissions.removeIf(p -> p.getUser().equals(user));
        if (removed || permsRemoved) touch();
    }

    public Set<GuildPermissions> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void setPermissions(UUID user, Set<Permission> permissions) {
        Objects.requireNonNull(user, "User id must not be null.");
        if (isNotMember(user)) throw new IllegalArgumentException("User is not a member of this guild.");

        this.permissions.removeIf(p -> p.getUser().equals(user));

        Set<Permission> granted = (permissions == null || permissions.isEmpty())
                ? DEFAULT_PERMISSIONS
                : EnumSet.copyOf(permissions);
        this.permissions.add(new GuildPermissions(getId(), user, granted));
        touch();
    }

    public Set<UUID> getBans() {
        return Collections.unmodifiableSet(bans);
    }

    public void addBan(UUID user) {
        Objects.requireNonNull(user, "User id must not be null.");
        if (isOwner(user)) throw new IllegalStateException("Cannot ban the guild owner.");
        boolean changed = bans.add(user);
        if (users.remove(user)) changed = true;
        if (this.permissions.removeIf(p -> p.getUser().equals(user))) changed = true;
        if (changed) touch();
    }

    public void removeBan(UUID user) {
        Objects.requireNonNull(user, "User id must not be null.");
        if (bans.remove(user)) touch();
    }

    public boolean isNotMember(UUID user) {
        Objects.requireNonNull(user, "User id must not be null.");
        return !users.contains(user);
    }

    public boolean isBanned(UUID user) {
        Objects.requireNonNull(user, "User id must not be null.");
        return bans.contains(user);
    }

    @Override
    public String toString() {
        return String.format("Guild[id=%s, name='%s', ownerId=%s, users=%d, bans=%d]",
                getId(), name, owner, users.size(), bans.size());
    }
}