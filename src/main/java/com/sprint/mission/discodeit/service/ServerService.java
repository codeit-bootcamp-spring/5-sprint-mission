package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Server;
import com.sprint.mission.discodeit.enums.serverentity.ServerLevel;
import com.sprint.mission.discodeit.enums.serverentity.ServerPerk;
import java.util.List;
import java.util.UUID;

public interface ServerService extends Service<Server> {
  boolean createServer(Server server);

  List<Server> findPublicServers();

  List<Server> findServersOwnedByUser(UUID userId);

  List<Server> findServersJoined(UUID userId);

  void updateName(UUID serverId, String name);

  void addChannel(UUID serverId, Channel channel);

  void removeChannel(UUID serverId, Channel channel);

  void clearChannels(UUID serverId);

  void addMember(UUID serverId, UUID memberId);

  void removeMember(UUID serverId, UUID memberId);

  void clearMembers(UUID serverId);

  void updateOwnerId(UUID serverId, UUID ownerId);

  void updatePublic(UUID serverId, boolean isPublic);

  void updateBoost(UUID serverId, long boost);

  void updateServerLevel(UUID serverId, ServerLevel level);

  void addPerk(UUID serverId, ServerPerk perk);

  void removePerk(UUID serverId, ServerPerk perk);

  void clearPerks(UUID serverId);
}
