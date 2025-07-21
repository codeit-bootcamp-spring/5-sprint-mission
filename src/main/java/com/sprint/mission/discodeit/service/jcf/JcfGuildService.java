package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.enums.Permission;
import com.sprint.mission.discodeit.service.GuildService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class JcfGuildService extends BaseJcfService<Guild> implements GuildService {
  private static final JcfGuildService instance = new JcfGuildService();

  private final UserService userService;

  private JcfGuildService() {
    this.userService = JcfUserService.getInstance();
  }

  public static JcfGuildService getInstance() {
    return instance;
  }

  @Override
  public Guild save(Guild guild) {
    userService.getOrThrow(guild.getOwnerId());
    if (findById(guild.getId()).isPresent()) {
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
    userService.getOrThrow(userId);
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
    userService.getOrThrow(member);
    update(guildId, g -> g.addMember(member));
  }

  @Override
  public void updateMemberPermissions(UUID guildId, UUID member, Set<Permission> permissions) {
    userService.getOrThrow(member);
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
