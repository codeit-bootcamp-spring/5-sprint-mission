package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Server;
import com.sprint.mission.discodeit.service.ServerService;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class JcfServerService extends JcfService<Server> implements ServerService {
  private static final JcfServerService instance = new JcfServerService();

  private JcfServerService() {}

  public static JcfServerService getInstance() {
    return instance;
  }

  @Override
  protected boolean idEquals(Server server, UUID id) {
    return server.getId().equals(id);
  }

  @Override
  public boolean createServer(Server server) {
    boolean exists = data.stream().anyMatch(s -> s.getId().equals(server.getId()));
    if (exists) {
      System.out.println("중복된 id가 존재합니다.");
      return false;
    }
    data.add(server);
    return true;
  }

  @Override
  public List<Server> findPublicServers() {
    return data.stream().filter(Server::isPublic).toList();
  }

  @Override
  public List<Server> findServersOwnedByUser(UUID userId) {
    return data.stream().filter(s -> s.getOwnerId().equals(userId)).toList();
  }

  @Override
  public List<Server> findServersJoined(UUID userId) {
    return data.stream().filter(s -> s.getMembers().contains(userId)).toList();
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
  public void updatePublic(UUID serverId, boolean isPublic) {
    update(serverId, s -> s.setPublic(isPublic));
  }

  @Override
  public void updateOwnerId(UUID serverId, UUID ownerId) {
    update(serverId, s -> s.setOwnerId(ownerId));
  }

  @Override
  public void updateName(UUID serverId, String name) {
    update(serverId, s -> s.setName(name));
  }

  @Override
  public void addMember(UUID serverId, UUID member) {
    update(serverId, s -> s.addMember(member));
  }

  @Override
  public void removeMember(UUID serverId, UUID member) {
    update(serverId, s -> s.removeMember(member));
  }

  @Override
  public void clearMembers(UUID serverId) {
    update(serverId, Server::clearMembers);
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
}
