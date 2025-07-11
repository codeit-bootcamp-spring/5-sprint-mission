package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Server;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.ServerLevel;
import com.sprint.mission.discodeit.enums.ServerPerk;
import com.sprint.mission.discodeit.service.ServerService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFServerService implements ServerService {
    private static final JCFServerService instance = new JCFServerService();

    private final List<Server> data;

    private JCFServerService() {
        data = new ArrayList<Server>();
    }

    public static JCFServerService getInstance() {
        return instance;
    }

    @Override
    public void createServer(Server server) {
        boolean exists = data.stream()
                .anyMatch(s -> s.getId().equals(server.getId()));
        if (exists) {
            System.out.println("중복된 id가 존재합니다.");
            return;
        }
        data.add(server);
    }

    @Override
    public Server findById(UUID id) {
        return data.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Server> findAll() {
        return data;
    }

    @Override
    public void updateChannels(Server server, List<Channel> channels) {
        data.stream()
                .filter(s -> s.getId().equals(server.getId()))
                .findFirst()
                .ifPresent(s -> {
                    s.setChannels(channels);
                    s.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateOwner(Server server, User owner) {
        data.stream()
                .filter(s -> s.getId().equals(server.getId()))
                .findFirst()
                .ifPresent(s -> {
                    s.setOwner(owner);
                    s.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateBoost(Server server, long boost) {
        data.stream()
                .filter(s -> s.getId().equals(server.getId()))
                .findFirst()
                .ifPresent(s -> {
                    s.setBoost(boost);
                    s.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateServerLevel(Server server, ServerLevel level) {
        data.stream()
                .filter(s -> s.getId().equals(server.getId()))
                .findFirst()
                .ifPresent(s -> {
                    s.setLevel(level);
                    s.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updatePerks(Server server, List<ServerPerk> perks) {
        data.stream()
                .filter(s -> s.getId().equals(server.getId()))
                .findFirst()
                .ifPresent(s -> {
                    s.setPerks(perks);
                    s.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void deleteById(UUID id) {
        data.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .ifPresent(data::remove);
    }
}
