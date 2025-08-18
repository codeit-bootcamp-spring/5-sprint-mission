package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.GuildPermissions;
import com.sprint.mission.discodeit.domain.enums.Permission;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GuildPermissionsRepository extends AbstractRepository<GuildPermissions> {

  boolean existsByGuildIdAndUserId(UUID guildId, UUID userId);

  Optional<GuildPermissions> findByGuildIdAndUserId(UUID guildId, UUID userId);

  List<GuildPermissions> findAllByGuildId(UUID guildId);

  List<GuildPermissions> findAllByUserId(UUID userId);

  List<GuildPermissions> findAllByGuildIdAndUserIdIn(UUID guildId, Collection<UUID> userIds);

  List<GuildPermissions> findAllByGuildIdAndPermission(UUID guildId, Permission permission);

  boolean softDeleteByGuildIdAndUserId(UUID guildId, UUID userId);

  int softDeleteAllByGuildId(UUID guildId);

  int softDeleteAllByUserId(UUID userId);
}
