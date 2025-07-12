package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Server;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.serverEntity.ServerLevel;
import com.sprint.mission.discodeit.enums.serverEntity.ServerPerk;

import java.util.List;
import java.util.UUID;

public interface ServerService {
    boolean createServer(Server server);

    Server findById(UUID id);

    List<Server> findAll();

    List<Server> findPublicServers();

    List<Server> findServersOwnedByUser(User user);

    List<Server> findServersJoined(User user);

    void updateName(Server server, String name);

    void updateIsPublic(Server server, Boolean isPublic);

    void updateChannels(Server server, List<Channel> channels);

    void updateOwner(Server server, User owner);

    void updateMembers(Server server, List<User> members);

    void updateBoost(Server server, long boost);

    void updateServerLevel(Server server, ServerLevel level);

    void updatePerks(Server server, List<ServerPerk> perks);

    void deleteById(UUID id);
}
