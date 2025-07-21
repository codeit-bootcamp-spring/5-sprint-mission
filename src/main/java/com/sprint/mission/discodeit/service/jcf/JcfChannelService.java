package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.enums.Permission;
import com.sprint.mission.discodeit.enums.channel.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.GuildService;
import java.util.Set;
import java.util.UUID;

public class JcfChannelService extends BaseJcfService<Channel> implements ChannelService {
  private static final JcfChannelService instance = new JcfChannelService();
  private final GuildService guildService;

  private JcfChannelService() {
    this.guildService = JcfGuildService.getInstance();
  }

  public static JcfChannelService getInstance() {
    return instance;
  }

  @Override
  public Channel save(Channel channel) {
    if (findById(channel.getId()) != null) {
      throw new IllegalArgumentException("중복된 id가 존재합니다.");
    }

    Guild guild = guildService.getOrThrow(channel.getGuildId());
    channel.setPermissionsToUser(guild.getOwnerId(), Set.of(Permission.ADMINISTRATOR));

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
}
