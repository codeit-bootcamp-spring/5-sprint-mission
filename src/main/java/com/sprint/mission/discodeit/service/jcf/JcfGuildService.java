package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.service.GuildService;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class JcfGuildService extends JcfService<Guild> implements GuildService {
  private static final JcfGuildService instance = new JcfGuildService();

  private JcfGuildService() {}

  public static JcfGuildService getInstance() {
    return instance;
  }

  @Override
  protected boolean idEquals(Guild guild, UUID id) {
    return guild.getId().equals(id);
  }

  @Override
  public boolean createGuild(Guild guild) {
    boolean exists = data.stream().anyMatch(s -> s.getId().equals(guild.getId()));
    if (exists) {
      System.out.println("중복된 id가 존재합니다.");
      return false;
    }
    data.add(guild);
    return true;
  }

  @Override
  public List<Guild> findPublicGuilds() {
    return data.stream().filter(Guild::isPublic).toList();
  }

  @Override
  public List<Guild> findGuildsOwnedByUser(UUID userId) {
    return data.stream().filter(s -> s.getOwnerId().equals(userId)).toList();
  }

  @Override
  public List<Guild> findGuildsJoined(UUID userId) {
    return data.stream().filter(s -> s.getMembers().contains(userId)).toList();
  }

  @Override
  public void update(UUID guildId, Consumer<Guild> updater) {
    Guild s = findById(guildId);
    if (s != null) {
      updater.accept(s);
      s.setUpdatedAt(System.currentTimeMillis());
    }
  }

  @Override
  public void updatePublic(UUID guildId, boolean isPublic) {
    update(guildId, s -> s.setPublic(isPublic));
  }

  @Override
  public void updateOwnerId(UUID guildId, UUID ownerId) {
    update(guildId, s -> s.setOwnerId(ownerId));
  }

  @Override
  public void updateName(UUID guildId, String name) {
    update(guildId, s -> s.setName(name));
  }

  @Override
  public void addMember(UUID guildId, UUID member) {
    update(guildId, s -> s.addMember(member));
  }

  @Override
  public void removeMember(UUID guildId, UUID member) {
    update(guildId, s -> s.removeMember(member));
  }

  @Override
  public void clearMembers(UUID guildId) {
    update(guildId, Guild::clearMembers);
  }

  @Override
  public void addChannel(UUID guildId, Channel channel) {
    update(guildId, s -> s.addChannel(channel));
  }

  @Override
  public void removeChannel(UUID guildId, Channel channel) {
    update(guildId, s -> s.removeChannel(channel));
  }

  @Override
  public void clearChannels(UUID guildId) {
    update(guildId, Guild::clearChannels);
  }
}
