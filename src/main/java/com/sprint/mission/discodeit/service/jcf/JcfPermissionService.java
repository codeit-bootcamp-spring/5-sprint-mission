package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Permission;
import com.sprint.mission.discodeit.enums.RoleType;
import com.sprint.mission.discodeit.service.PermissionService;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JcfPermissionService implements PermissionService {
  @Override
  public Set<Permission> resolveUserPermissions(
      UUID guildId, UUID channelId, UUID userId, Set<RoleType> roleTypes) {
    JcfUserService userService = JcfUserService.getInstance();
    JcfGuildService guildService = JcfGuildService.getInstance(userService);
    JcfChannelService channelService = JcfChannelService.getInstance();
    final Guild guild = guildService.findById(guildId);
    final Channel channel = channelService.findById(channelId);
    final User user = userService.findById(userId);

    Set<Permission> userOverride = channel.getUserPermissions(userId);
    if (!userOverride.isEmpty()) {
      return Collections.unmodifiableSet(userOverride);
    }

    Set<Permission> channelOverride = new HashSet<>();
    roleTypes.stream().map(channel::getRolePermissions).forEach(channelOverride::addAll);
    if (!channelOverride.isEmpty()) {
      return Collections.unmodifiableSet(channelOverride);
    }

    // Set<Permission> guildPermissions = new HashSet<>();
    // roleTypes.stream().map(guild::getRolePermissions).forEach(channelOverride::addAll);
    // if (!guildPermissions.isEmpty()) {
    //   return guildPermissions;
    // }
    return Collections.emptySet();
  }

  @Override
  public Set<Permission> resolveUserPermissions(
      UUID guildId, UUID channelId, Set<RoleType> roleTypes) {
    return Set.of();
  }

  @Override
  public Set<Permission> resolveUserPermissions(UUID guildId, Set<RoleType> roleTypes) {
    return Set.of();
  }

  @Override
  public boolean hasPermission(UUID guildId, UUID channelId, UUID userId, Permission permission) {
    return false;
    // Set<Permission> perms = resolveUserPermissions(guild, channel, userId, userRoles);
    // return perms.contains(permission) || perms.contains(Permission.ADMINISTRATOR);
  }

  @Override
  public boolean hasPermission(UUID guildId, UUID channelId, Permission permission) {
    return false;
  }

  @Override
  public boolean hasPermission(UUID guildId, Permission permission) {
    return false;
  }
}
