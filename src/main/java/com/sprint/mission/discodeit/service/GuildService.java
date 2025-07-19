package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Guild;
import java.util.List;
import java.util.UUID;

public interface GuildService extends Service<Guild> {
  Guild create(Guild guild);

  List<Guild> findPublicGuilds();

  List<Guild> findGuildsOwnedByUser(UUID userId);

  List<Guild> findGuildsJoined(UUID userId);

  void updateName(UUID guildId, String name);

  void addChannel(UUID guildId, Channel channel);

  void removeChannel(UUID guildId, Channel channel);

  void addMember(UUID guildId, UUID member);

  void removeMember(UUID guildId, UUID member);

  void updateOwnerId(UUID guildId, UUID ownerId);

  void updatePublic(UUID guildId, boolean isPublic);
}
