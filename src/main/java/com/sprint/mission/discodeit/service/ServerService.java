package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Server;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.ServerLevel;
import com.sprint.mission.discodeit.enums.ServerPerk;

import java.util.List;
import java.util.UUID;

public interface ServerService {
    boolean createServer(Server server);

    Server findById(UUID id);

    List<Server> findAll();

    void updateChannels(Server server, List<Channel> channels);

    void updateOwner(Server server, User owner);

    void updateBoost(Server server, long boost);

    void updateServerLevel(Server server, ServerLevel level);

    void updatePerks(Server server, List<ServerPerk> perks);

    void deleteById(UUID id);
}
