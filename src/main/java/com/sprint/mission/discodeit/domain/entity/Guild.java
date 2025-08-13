package com.sprint.mission.discodeit.domain.entity;

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

    private static final Set<Permission> DEFAULT_PERMISSIONS =
            Collections.unmodifiableSet(EnumSet.of(Permission.READ_MESSAGES, Permission.SEND_MESSAGES));

    private String name;
    private boolean discoverable;
    private UUID ownerId;

    private final Set<UUID> userIds = new HashSet<>();
    private final Set<GuildPermissions> permissions = new HashSet<>();
    private final Set<UUID> channelIds = new LinkedHashSet<>();
    private final Set<UUID> bannedUserIds = new HashSet<>();

    public Guild(String name, boolean discoverable, UUID ownerId) {
        setName(name);
        setDiscoverable(discoverable);
        addUser(Objects.requireNonNull(ownerId, "ownerId must not be null."), Set.of(Permission.ADMINISTRATOR));
        this.ownerId = ownerId;
        touch();
    }

    public void setName(String name) {
        String v = Validators.validateGuildName(name);
        if (!Objects.equals(this.name, v)) {
            this.name = v;
            touch();
        }
    }

    public void setDiscoverable(boolean discoverable) {
        if (this.discoverable != discoverable) {
            this.discoverable = discoverable;
            touch();
        }
    }

    public void setOwnerId(UUID userId) {
        Objects.requireNonNull(userId, "ownerId must not be null.");
        if (isBanned(userId)) throw new IllegalStateException("Banned user cannot be the owner.");
        if (isNotMember(userId)) throw new IllegalArgumentException("User is not a member of this guild.");

        if (Objects.equals(this.ownerId, userId)) return; // 변경 없음

        UUID prev = this.ownerId;

        setPermissions(userId, Set.of(Permission.ADMINISTRATOR));

        if (prev != null && userIds.contains(prev)) {
            setPermissions(prev, DEFAULT_PERMISSIONS);
        }

        this.ownerId = userId;
    }

    public Set<UUID> getChannelIds() {
        return Collections.unmodifiableSet(channelIds);
    }

    public void addChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId must not be null.");
        if (channelIds.add(channelId)) touch();
    }

    public void removeChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId must not be null.");
        if (channelIds.remove(channelId)) touch();
    }

    public Set<UUID> getUserIds() {
        return Collections.unmodifiableSet(userIds);
    }

    public boolean isOwner(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null.");
        return userId.equals(ownerId);
    }

    public boolean isNotMember(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null.");
        return !userIds.contains(userId);
    }

    public boolean isBanned(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null.");
        return bannedUserIds.contains(userId);
    }

    public void addUser(UUID userId) {
        addUser(userId, DEFAULT_PERMISSIONS);
    }

    public void addUser(UUID userId, Set<Permission> permissions) {
        Objects.requireNonNull(userId, "userId must not be null.");
        if (isBanned(userId)) throw new IllegalStateException("User is banned from this guild.");

        boolean added = userIds.add(userId);
        setPermissions(userId, permissions);
        if (added) touch();
    }

    public void removeUser(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null.");
        if (isOwner(userId))
            throw new IllegalStateException("Cannot remove the guild owner. Transfer ownership first.");

        boolean removedUser = userIds.remove(userId);
        boolean removedPerm = permissions.removeIf(p -> p.getUserId().equals(userId));
        if (removedUser || removedPerm) touch();
    }

    public Set<GuildPermissions> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void setPermissions(UUID userId, Set<Permission> newPermissions) {
        Objects.requireNonNull(userId, "userId must not be null.");
        if (isNotMember(userId)) throw new IllegalArgumentException("User is not a member of this guild.");
        if (isBanned(userId)) throw new IllegalStateException("Banned user cannot have permissions.");

        Set<Permission> granted = (newPermissions == null || newPermissions.isEmpty())
                ? EnumSet.copyOf(DEFAULT_PERMISSIONS)
                : EnumSet.copyOf(newPermissions);

        if (isOwner(userId)) {
            granted.add(Permission.ADMINISTRATOR);
        }

        granted = Collections.unmodifiableSet(granted);

        boolean changed = permissions.removeIf(p -> p.getUserId().equals(userId));
        changed |= permissions.add(new GuildPermissions(getId(), userId, granted));

        if (changed) touch();
    }

    public Set<UUID> getBannedUserIds() {
        return Collections.unmodifiableSet(bannedUserIds);
    }

    public void addBan(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null.");
        if (isOwner(userId)) throw new IllegalStateException("Cannot ban the guild owner.");

        boolean changed = bannedUserIds.add(userId);
        if (userIds.remove(userId)) changed = true;
        if (permissions.removeIf(p -> p.getUserId().equals(userId))) changed = true;

        if (changed) touch();
    }

    public void removeBan(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null.");
        if (bannedUserIds.remove(userId)) touch();
    }

    @Override
    public String toString() {
        return "Guild[id=%s, name='%s', ownerId=%s, users=%d, bans=%d]"
                .formatted(getId(), name, ownerId, userIds.size(), bannedUserIds.size());
    }
}
