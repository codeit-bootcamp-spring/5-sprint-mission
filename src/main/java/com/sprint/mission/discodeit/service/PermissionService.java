package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.enums.Permission;
import com.sprint.mission.discodeit.enums.RoleType;

import java.util.Set;
import java.util.UUID;

public interface PermissionService {
    Set<Permission> resolveUserPermissions(UUID guildId, UUID channelId, UUID userId, Set<RoleType> roleTypes);

    Set<Permission> resolveUserPermissions(UUID guildId, UUID channelId, Set<RoleType> roleTypes);

    Set<Permission> resolveUserPermissions(UUID guildId, Set<RoleType> roleTypes);

    boolean hasPermission(UUID guildId, UUID channelId, UUID userId, Permission permission);

    boolean hasPermission(UUID guildId, UUID channelId, Permission permission);

    boolean hasPermission(UUID guildId, Permission permission);
}
