package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.enums.channel.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.UUID;
import java.util.function.Consumer;

public class JcfChannelService extends JcfService<Channel> implements ChannelService {
  private static final JcfChannelService instance = new JcfChannelService();

  private JcfChannelService() {}

  public static JcfChannelService getInstance() {
    return instance;
  }

  protected boolean idEquals(Channel channel, UUID id) {
    return channel.getId().equals(id);
  }

  @Override
  public boolean createChannel(Channel channel) {
    boolean exists = data.stream().anyMatch(c -> c.getId().equals(channel.getId()));
    if (exists) {
      System.out.println("중복된 id가 존재합니다.");
      return false;
    }
    data.add(channel);
    return true;
  }

  @Override
  public void update(UUID channelId, Consumer<Channel> updater) {
    Channel c = findById(channelId);
    if (c != null) {
      updater.accept(c);
      c.setUpdatedAt(System.currentTimeMillis());
    }
  }

  @Override
  public void updateName(UUID channelId, String name) {
    update(channelId, c -> c.setName(name));
  }

  @Override
  public void updateChannelType(UUID channelId, ChannelType channelType) {
    update(channelId, c -> c.setChannelType(channelType));
  }

  @Override
  public void updatePublic(UUID channelId, boolean isPublic) {
    update(channelId, c -> c.setPublic(isPublic));
  }

  @Override
  public void addJoinedUser(UUID channelId, UUID userId) {
    update(channelId, c -> c.addJoinedUser(userId));
  }

  @Override
  public void removeJoinedUser(UUID channelId, UUID userId) {
    update(channelId, c -> c.removeJoinedUser(userId));
  }

  @Override
  public void clearJoinedUsers(UUID channelId) {
    update(channelId, Channel::clearJoinedUsers);
  }
}
