package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.Permission;
import com.sprint.mission.discodeit.utility.Validators;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Guild extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private boolean discoverable;
    private UUID ownerId;
    private final Map<UUID, Set<Permission>> members = new HashMap<>();
    private final List<Channel> channels = new ArrayList<>();
    private final Set<UUID> bans = new HashSet<>();

    private static final Set<Permission> DEFAULT_PERMISSIONS =
            EnumSet.of(Permission.READ_MESSAGES, Permission.SEND_MESSAGES);

    public Guild(String name, boolean discoverable, UUID ownerId) {
        setName(name);
        setDiscoverable(discoverable);
        setOwnerId(ownerId);
        addMember(ownerId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Validators.validateGuildName(name);
    }

    public boolean isDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(boolean discoverable) {
        this.discoverable = discoverable;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID must not be null.");
        }
        this.ownerId = ownerId;
        updateMemberPermissions(ownerId, Set.of(Permission.ADMINISTRATOR));
    }

    public boolean isOwner(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }
        return id.equals(ownerId);
    }

    public List<Channel> getChannels() {
        return Collections.unmodifiableList(channels);
    }

    public void addChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel must not be null.");
        }
        if (!channels.contains(channel)) {
            channels.add(channel);
        }
    }

    public void removeChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel must not be null.");
        }
        channels.remove(channel);
    }

    public Map<UUID, Set<Permission>> getMembers() {
        return Collections.unmodifiableMap(members);
    }

    public void addMember(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }
        members.putIfAbsent(
                id,
                id.equals(ownerId)
                        ? EnumSet.of(Permission.ADMINISTRATOR)
                        : EnumSet.copyOf(DEFAULT_PERMISSIONS));
    }

    public void updateMemberPermissions(UUID id, Set<Permission> permissions) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }
        if (permissions == null) {
            throw new IllegalArgumentException("Permissions must not be null.");
        }
        if (permissions.isEmpty()) {
            throw new IllegalArgumentException("Permissions must not be empty.");
        }
        if (isNotMember(id)) {
            throw new IllegalArgumentException("User is not a member of this guild.");
        }
        members.put(id, EnumSet.copyOf(permissions));
    }

    public void removeMember(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }
        members.remove(id);
    }

    public Set<UUID> getBans() {
        return Collections.unmodifiableSet(bans);
    }

    public void addBan(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }
        bans.add(id);
    }

    public void removeBan(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }
        bans.remove(id);
    }

    public boolean isNotMember(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }
        return !members.containsKey(id);
    }

    public boolean isBanned(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }
        return bans.contains(id);
    }

    @Override
    public String toString() {
        return "Guild{"
                + "name='"
                + name
                + '\''
                + ", discoverable="
                + discoverable
                + ", ownerId="
                + ownerId
                + ", members="
                + members
                + ", channels="
                + channels
                + ", bans="
                + bans
                + '}';
    }
}
