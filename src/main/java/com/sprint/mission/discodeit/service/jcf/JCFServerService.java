package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Server;
import com.sprint.mission.discodeit.enums.serverEntity.ServerLevel;
import com.sprint.mission.discodeit.enums.serverEntity.ServerPerk;
import com.sprint.mission.discodeit.service.ServerService;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class JCFServerService extends JCFService<Server> implements ServerService {
    private static final JCFServerService instance = new JCFServerService();

    public static JCFServerService getInstance() {
        return instance;
    }

    @Override
    protected boolean idEquals(Server server, UUID id) {
        return server.getId().equals(id);
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
    public List<Server> findPublicServers() {
        return data.stream()
                .filter(Server::isPublic)
                .toList();
    }

    @Override
    public List<Server> findServersOwnedByUser(UUID userId) {
        return data.stream()
                .filter(s -> s.getOwnerId().equals(userId))
                .toList();
    }

    @Override
    public List<Server> findServersJoined(UUID userId) {
        return data.stream()
                .filter(s -> s.getMembers().contains(userId))
                .toList();
    }

    @Override
    public void update(UUID serverId, Consumer<Server> updater) {
        Server s = findById(serverId);
        if (s != null) {
            updater.accept(s);
            s.setUpdatedAt(System.currentTimeMillis());
        }
    }

    @Override
    public void updateName(UUID serverId, String name) {
        update(serverId, s -> s.setName(name));
    }

    @Override
    public void addChannel(UUID serverId, Channel channel) {
        update(serverId, s -> s.addChannel(channel));
    }

    @Override
    public void removeChannel(UUID serverId, Channel channel) {
        update(serverId, s -> s.removeChannel(channel));
    }

    @Override
    public void clearChannels(UUID serverId) {
        update(serverId, Server::clearChannels);
    }

    @Override
    public void addMember(UUID serverId, UUID memberId) {
        update(serverId, s -> s.addMember(memberId));
    }

    @Override
    public void removeMember(UUID serverId, UUID memberId) {
        update(serverId, s -> s.removeMember(memberId));
    }

    @Override
    public void clearMembers(UUID serverId) {
        update(serverId, Server::clearMembers);
    }

    @Override
    public void addPerk(UUID serverId, ServerPerk perk) {
        update(serverId, s -> s.addPerk(perk));
    }

    @Override
    public void removePerk(UUID serverId, ServerPerk perk) {
        update(serverId, s -> s.removePerk(perk));
    }

    @Override
    public void clearPerks(UUID serverId) {
        update(serverId, Server::clearPerks);
    }

    @Override
    public void updatePublic(UUID serverId, boolean isPublic) {
        update(serverId, s -> s.setPublic(isPublic));
    }

    @Override
    public void updateOwnerId(UUID serverId, UUID ownerId) {
        update(serverId, s -> s.setOwnerId(ownerId));
    }

    @Override
    public void updateBoost(UUID serverId, long boost) {
        update(serverId, s -> s.setBoost(boost));
    }

    @Override
    public void updateServerLevel(UUID serverId, ServerLevel level) {
        update(serverId, s -> s.setLevel(level));
    }
}
