package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Server;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.serverEntity.ServerLevel;
import com.sprint.mission.discodeit.enums.serverEntity.ServerPerk;
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
    // @VisibleForTesting
    public void reset() {
        JCFServerService.getInstance().data.clear();
    }

    @Override
    public boolean createServer(Server server) {
        boolean exists = data.stream()
                .anyMatch(s -> s.getId().equals(server.getId()));
        if (exists) {
            System.out.println("중복된 id가 존재합니다.");
            return false;
        }
        data.add(server);
        return true;
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
    public List<Server> findPublicServers() {
        return data.stream()
                .filter(Server::isPublic)
                .toList();
    }

    @Override
    public List<Server> findServersOwnedByUser(User user) {
        return data.stream()
                .filter(s -> s.getOwner().getId().equals(user.getId()))
                .toList();
    }

    @Override
    public List<Server> findServersJoined(User user) {
        return data.stream()
                .filter(s -> s.getMembers().contains(user))
                .toList();
    }

    @Override
    public void updateName(Server server, String name) {
        data.stream()
                .filter(s -> s.getId().equals(server.getId()))
                .findFirst()
                .ifPresent(s -> {
                    s.setName(name);
                    s.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateIsPublic(Server server, boolean isPublic) {
        data.stream()
                .filter(s -> s.getId().equals(server.getId()))
                .findFirst()
                .ifPresent(s -> {
                    s.setPublic(isPublic);
                    s.setUpdatedAt(System.currentTimeMillis());
                });
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
    public void updateMembers(Server server, List<User> members) {
        data.stream()
                .filter(s -> s.getId().equals(server.getId()))
                .findFirst()
                .ifPresent(s -> {
                    s.setMembers(members);
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
