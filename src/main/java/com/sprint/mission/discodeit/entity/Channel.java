package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.Permission;
import com.sprint.mission.discodeit.enums.RoleType;
import com.sprint.mission.discodeit.enums.channel.ChannelType;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Channel extends AbstractBaseEntity {
  private final long createdAt;
  private long updatedAt;
  private final UUID guildId;
  private String name;
  private ChannelType type;
  private Boolean isPublic;
  private final Set<UUID> joinedUsers;
  private final Map<RoleType, Set<Permission>> rolePermissions;
  private final Map<UUID, Set<Permission>> userPermissions;

  public Channel(UUID guildId, String name, ChannelType type) {
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
    this.guildId = guildId;
    this.name = name;
    this.type = type;
    this.isPublic = true;
    this.joinedUsers = new HashSet<>();
    this.rolePermissions = new HashMap<>();
    this.userPermissions = new HashMap<>();
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(long updatedAt) {
    this.updatedAt = updatedAt;
  }

  public UUID getGuildId() {
    return guildId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ChannelType getType() {
    return type;
  }

  public void setType(ChannelType type) {
    this.type = type;
  }

  public Boolean getPublic() {
    return isPublic;
  }

  public void setPublic(Boolean isPublic) {
    this.isPublic = isPublic;
  }

  public Set<UUID> getJoinedUsers() {
    return Collections.unmodifiableSet(joinedUsers);
  }

  public void addJoinedUser(UUID joinedUserId) {
    joinedUsers.add(joinedUserId);
  }

  public void removeJoinedUser(UUID joinedUserId) {
    joinedUsers.remove(joinedUserId);
  }

  public void clearJoinedUsers() {
    joinedUsers.clear();
  }

  public Map<UUID, Set<Permission>> getUserPermissionMap() {
    return Collections.unmodifiableMap(userPermissions);
  }

  public Set<Permission> getUserPermissions(UUID userId) {
    return Collections.unmodifiableSet(
        userPermissions.getOrDefault(userId, Collections.emptySet()));
  }

  public void setPermissionsToUser(UUID userId, Set<Permission> permissions) {
    userPermissions.put(userId, new HashSet<>(permissions));
  }

  public void clearUserPermission(UUID userId) {
    userPermissions.remove(userId);
  }

  public void clearUserPermissionMap() {
    userPermissions.clear();
  }

  public Map<RoleType, Set<Permission>> getRolePermissionMap() {
    return Collections.unmodifiableMap(rolePermissions);
  }

  public Set<Permission> getRolePermissions(RoleType roleType) {
    Set<Permission> permissions = rolePermissions.getOrDefault(roleType, Collections.emptySet());
    return Collections.unmodifiableSet(permissions);
  }

  public void setPermissionsToRoleType(RoleType roleType, Set<Permission> permissions) {
    rolePermissions.put(roleType, new HashSet<>(permissions));
  }

  public void clearRolePermission(RoleType roleType) {
    rolePermissions.remove(roleType);
  }

  public void clearRolePermissionMap() {
    rolePermissions.clear();
  }
}
