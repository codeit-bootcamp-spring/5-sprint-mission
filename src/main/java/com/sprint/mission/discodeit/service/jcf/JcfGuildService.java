package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.enums.Permission;
import com.sprint.mission.discodeit.service.GuildService;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class JcfGuildService extends BaseJcfService<Guild> implements GuildService {
  private static final JcfGuildService instance = new JcfGuildService();

  public static JcfGuildService getInstance() {
    return instance;
  }

  private JcfGuildService() {}

  @Override
  public Guild create(Guild guild) {
    if (findById(guild.getId()) != null) {
      throw new IllegalArgumentException("중복된 id가 존재합니다.");
    }
    data.add(guild);
    return guild;
  }

  @Override
  public List<Guild> findDiscoverableGuilds() {
    return data.stream().filter(Guild::isDiscoverable).toList();
  }

  @Override
  public List<Guild> findGuildsOwnedByUser(UUID userId) {
    return data.stream().filter(g -> g.getOwnerId().equals(userId)).toList();
  }

  @Override
  public void updateDiscoverable(UUID guildId, boolean discoverable) {
    update(guildId, g -> g.setDiscoverable(discoverable));
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
  public void updateMemberPermissions(UUID guildId, UUID member, Set<Permission> permissions) {
    update(guildId, g -> g.updateMemberPermissions(member, permissions));
  }

  @Override
  public void removeMember(UUID guildId, UUID member) {
    update(guildId, g -> g.removeMember(member));
  }

  @Override
  public void addChannel(UUID guildId, Channel channel) {
    update(guildId, g -> g.addChannel(channel));
  }

  @Override
  public void removeChannel(UUID guildId, Channel channel) {
    update(guildId, g -> g.removeChannel(channel));
  }
}
