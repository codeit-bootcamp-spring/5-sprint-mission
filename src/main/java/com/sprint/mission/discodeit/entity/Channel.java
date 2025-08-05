package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.Permission;
import com.sprint.mission.discodeit.enums.RoleType;
import com.sprint.mission.discodeit.enums.channel.ChannelType;
import com.sprint.mission.discodeit.utility.Validators;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Channel extends BaseEntity {
    private String name;
    private ChannelType type;
    private Boolean isPublic;
    private final UUID guildId;
    private final Set<UUID> joinedUsers = new HashSet<>();
    private final Map<RoleType, Set<Permission>> rolePermissions = new HashMap<>();
    private final Map<UUID, Set<Permission>> userPermissions = new HashMap<>();

    public Channel(UUID guildId, String name, ChannelType type) {
        if (guildId == null) throw new IllegalArgumentException("Guild ID must not be null.");
        this.guildId = guildId;
        setName(name);
        setType(type);
        this.isPublic = true;
    }

    public UUID getGuildId() {
        return guildId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Validators.validateChannelName(name);
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        if (type == null) throw new IllegalArgumentException("Channel type must not be null.");
        this.type = type;
    }

    public Boolean isPublic() {
        return isPublic;
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Set<UUID> getJoinedUsers() {
        return Collections.unmodifiableSet(joinedUsers);
    }

    public void addJoinedUser(UUID id) {
        if (id == null) throw new IllegalArgumentException("User ID must not be null.");
        joinedUsers.add(id);
    }

    public void removeJoinedUser(UUID id) {
        if (id == null) throw new IllegalArgumentException("User ID must not be null.");
        joinedUsers.remove(id);
    }

    public Map<UUID, Set<Permission>> getUserPermissionMap() {
        return Collections.unmodifiableMap(userPermissions);
    }

    public Set<Permission> getUserPermissions(UUID id) {
        if (id == null) throw new IllegalArgumentException("User ID must not be null.");
        return Collections.unmodifiableSet(userPermissions.getOrDefault(id, Collections.emptySet()));
    }

    public void setPermissionsToUser(UUID id, Set<Permission> permissions) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }
        if (permissions == null || permissions.isEmpty()) {
            throw new IllegalArgumentException("Permissions must not be null.");
        }
        userPermissions.put(id, new HashSet<>(permissions));
    }

    public Map<RoleType, Set<Permission>> getRolePermissionMap() {
        return Collections.unmodifiableMap(rolePermissions);
    }

    public Set<Permission> getRolePermissions(RoleType roleType) {
        if (roleType == null) throw new IllegalArgumentException("Role type must not be null.");
        Set<Permission> permissions = rolePermissions.getOrDefault(roleType, Collections.emptySet());
        return Collections.unmodifiableSet(permissions);
    }

    public void setPermissionsToRoleType(RoleType roleType, Set<Permission> permissions) {
        if (roleType == null) throw new IllegalArgumentException("Role type must not be null.");
        rolePermissions.put(roleType, new HashSet<>(permissions));
    }

    @Override
    public String toString() {
        return "Channel{"
                + "guildId="
                + guildId
                + ", name='"
                + name
                + '\''
                + ", type="
                + type
                + ", isPublic="
                + isPublic
                + ", joinedUsers="
                + joinedUsers
                + '}';
    }
}
