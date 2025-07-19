package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.enums.Permission;
import com.sprint.mission.discodeit.enums.channel.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.Set;
import java.util.UUID;

public class JcfChannelService extends JcfService<Channel> implements ChannelService {
  private static final JcfChannelService instance = new JcfChannelService();

  private JcfChannelService() {}

  public static JcfChannelService getInstance() {
    return instance;
  }

  @Override
  public Channel createChannel(Channel channel, UUID ownerId) {
    if (channel == null) {
      System.out.println("channel == null");
      return null;
    }
    boolean exists = data.stream().anyMatch(c -> c.getId().equals(channel.getId()));
    if (exists) {
      System.out.println("이미 존재하는 채널입니다.");
      return null;
    }
    channel.setPermissionsToUser(ownerId, Set.of(Permission.ADMINISTRATOR));
    data.add(channel);
    return channel;
  }

  @Override
  public void updateName(UUID channelId, String name) {
    update(channelId, c -> c.setName(name));
  }

  @Override
  public void updateType(UUID channelId, ChannelType type) {
    update(channelId, c -> c.setType(type));
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
