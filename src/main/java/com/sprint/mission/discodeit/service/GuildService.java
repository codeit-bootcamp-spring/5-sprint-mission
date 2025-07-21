package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.enums.Permission;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface GuildService extends BaseService<Guild> {
  List<Guild> findDiscoverableGuilds();

  List<Guild> findGuildsOwnedByUser(UUID userId);

  void updateName(UUID guildId, String name);

  void updateDiscoverable(UUID guildId, boolean discoverable);

  void updateOwnerId(UUID guildId, UUID ownerId);

  void addChannel(UUID guildId, Channel channel);

  void removeChannel(UUID guildId, Channel channel);

  void addMember(UUID guildId, UUID member);

  void updateMemberPermissions(UUID guildId, UUID member, Set<Permission> permissions);

  void removeMember(UUID guildId, UUID member);

  void delete(UUID guildId);
}
