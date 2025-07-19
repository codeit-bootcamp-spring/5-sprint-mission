package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.service.GuildService;
import java.util.List;
import java.util.UUID;

public class JcfGuildService extends JcfService<Guild> implements GuildService {
  private static final JcfGuildService instance = new JcfGuildService();

  private JcfGuildService() {}

  public static JcfGuildService getInstance() {
    return instance;
  }

  @Override
  public Guild create(Guild guild) {
    if (findById(guild.getId()) != null) {
      System.out.println("중복된 id가 존재합니다.");
      return null;
    }
    data.add(guild);
    return guild;
  }

  @Override
  public List<Guild> findPublicGuilds() {
    return data.stream().filter(Guild::isPublic).toList();
  }

  @Override
  public List<Guild> findGuildsOwnedByUser(UUID userId) {
    return data.stream()
      .filter(g -> g.getOwnerId().equals(userId))
      .toList();
  }

  @Override
  public List<Guild> findGuildsJoined(UUID userId) {
    return data.stream()
      .filter(g -> g.getMembers().contains(userId))
      .toList();
  }

  @Override
  public void updatePublic(UUID guildId, boolean isPublic) {
    update(guildId, g -> g.setPublic(isPublic));
  }

  @Override
  public void updateOwnerId(UUID guildId, UUID ownerId) {
    update(guildId, g -> g.setOwnerId(ownerId));
  }

  @Override
  public void updateName(UUID guildId, String name) {
    update(guildId, g -> g.setName(name));
  }

  @Override
  public void addMember(UUID guildId, UUID member) {
    update(guildId, g -> g.addMember(member));
  }

  @Override
  public void removeMember(UUID guildId, UUID member) {
    update(guildId, g -> g.removeMember(member));
  }

  @Override
  public void clearMembers(UUID guildId) {
    update(guildId, Guild::clearMembers);
  }

  @Override
  public void addChannel(UUID guildId, Channel channel) {
    update(guildId, g -> g.addChannel(channel));
  }

  @Override
  public void removeChannel(UUID guildId, Channel channel) {
    update(guildId, g -> g.removeChannel(channel));
  }

  @Override
  public void clearChannels(UUID guildId) {
    update(guildId, Guild::clearChannels);
  }
}
