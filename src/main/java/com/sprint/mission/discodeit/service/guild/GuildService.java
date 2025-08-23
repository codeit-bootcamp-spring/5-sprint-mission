package com.sprint.mission.discodeit.service.guild;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Guild;
import com.sprint.mission.discodeit.domain.entity.GuildPermissions;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.enums.Permission;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.GuildRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuildService {

  private final GuildRepository guildRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  private void update(UUID id, Consumer<Guild> updater) {
    Guild g = guildRepository.getOrThrow(id);
    updater.accept(g);
    guildRepository.save(g);
  }

  // public GuildResponse create(GuildCreateRequest req) {
  //   Objects.requireNonNull(req, "req must not be null");
  //   Objects.requireNonNull(req.ownerId(), "ownerId must not be null");
  //
  //   Guild saved = guildRepository.save(new Guild(
  //       req.name(), req.discoverable(), req.ownerId()
  //   ));
  //
  //   return toGuildResponse(saved);
  // }
  //
  // public GuildResponse find(UUID guildId) {
  //   return guildRepository.findById(guildId)
  //       .map(GuildMapper::toGuildResponse)
  //       .orElseThrow(() -> new NotFoundException("Guild with id %s not found".formatted(guildId)));
  // }
  //
  // public List<GuildResponse> findAll() {
  //   return guildRepository.findAll().stream()
  //       .map(GuildMapper::toGuildResponse)
  //       .toList();
  // }
  //
  // public List<GuildResponse> findGuildsJoinedByUser(UUID userId) {
  //   Set<UUID> ids = userRepository.getOrThrow(userId).getGuildIds();
  //   return guildRepository.findAllById(ids).stream()
  //       .map(GuildMapper::toGuildResponse)
  //       .toList();
  // }

  public void deleteGuild(UUID guildId, UUID ownerId) {
    Guild guild = guildRepository.getOrThrow(guildId);
    if (!guild.isOwner(ownerId)) {
      throw new IllegalArgumentException("삭제할 권한이 없습니다");
    }
    for (UUID uid : new HashSet<>(guild.getUserIds())) {
      User user = userRepository.getOrThrow(uid);
      user.leaveGuild(guildId);
      userRepository.save(user);
    }
    guildRepository.softDeleteById(guildId);
  }

  public void updateName(UUID guildId, String name) {
    update(guildId, g -> g.setName(name));
  }

  public void updateDiscoverable(UUID guildId, boolean discoverable) {
    update(guildId, g -> g.setDiscoverable(discoverable));
  }

  public void updateOwnerId(UUID guildId, UUID oldOwnerId, UUID newOwnerId) {
    userRepository.getOrThrow(oldOwnerId);
    userRepository.getOrThrow(newOwnerId);
    Guild guild = guildRepository.getOrThrow(guildId);
    if (!guild.isOwner(oldOwnerId)) {
      throw new IllegalArgumentException("서버 주인 변경 권한이 없습니다");
    }
    if (guild.isNotMember(newOwnerId)) {
      throw new IllegalArgumentException("해당 유저는 이 서버의 멤버가 아닙니다");
    }
    guild.setOwnerId(newOwnerId);
    guildRepository.save(guild);
  }


  public List<Channel> getChannels(UUID guildId) {
    Set<UUID> ids = guildRepository.getOrThrow(guildId).getChannelIds();
    return channelRepository.findAllById(ids);
  }

  public void addMember(UUID guildId, UUID userId) {
    userRepository.getOrThrow(userId);
    Guild guild = guildRepository.getOrThrow(guildId);
    User user = userRepository.getOrThrow(userId);
    user.joinGuild(guildId);
    userRepository.save(user);

    guild.addUser(userId);
    guildRepository.save(guild);
  }

  public void removeMember(UUID guildId, UUID userId) {
    Guild guild = guildRepository.getOrThrow(guildId);
    if (guild.isOwner(userId)) {
      throw new IllegalArgumentException("서버 주인 변경 후 퇴장이 가능합니다");
    }
    User user = userRepository.getOrThrow(userId);
    user.leaveGuild(guildId);
    userRepository.save(user);

    guild.removeUser(userId);
    guildRepository.save(guild);
  }

  public boolean isMember(UUID guildId, UUID userId) {
    return !guildRepository.getOrThrow(guildId).isNotMember(userId);
  }

  public Set<UUID> getMemberIds(UUID guildId) {
    return Collections.unmodifiableSet(guildRepository.getOrThrow(guildId).getUserIds());
  }

  public int getMemberCount(UUID guildId) {
    return guildRepository.getOrThrow(guildId).getUserIds().size();
  }

  public Set<GuildPermissions> getMemberPermissions(UUID guildId, UUID userId) {
    return guildRepository.getOrThrow(guildId).getPermissions().stream()
        .filter(p -> p.getUserId().equals(userId))
        .collect(Collectors.toSet());
  }

  public void updateMemberPermissions(UUID guildId, UUID userId, Set<Permission> permissions) {
    Guild guild = guildRepository.getOrThrow(guildId);
    if (guild.isNotMember(userId)) {
      throw new IllegalArgumentException("해당 유저는 이 서버의 멤버가 아닙니다.");
    }
    guild.setPermissions(userId, permissions);
    guildRepository.save(guild);
  }

  public void addBan(UUID guildId, UUID userId) {
    userRepository.getOrThrow(userId);
    Guild guild = guildRepository.getOrThrow(guildId);
    if (guild.isOwner(userId)) {
      throw new IllegalStateException("서버 주인은 밴할 수 없습니다.");
    }
    if (guild.isBanned(userId)) {
      throw new IllegalStateException("이미 밴된 유저입니다.");
    }
    guild.addBan(userId);
    guildRepository.save(guild);
  }

  public void removeBan(UUID guildId, UUID userId) {
    Guild guild = guildRepository.getOrThrow(guildId);
    guild.removeBan(userId);
    guildRepository.save(guild);
  }

  public boolean isBanned(UUID guildId, UUID userId) {
    return guildRepository.getOrThrow(guildId).isBanned(userId);
  }

  public Set<UUID> getBannedUsers(UUID guildId) {
    return guildRepository.getOrThrow(guildId).getBannedUserIds();
  }

  public int getBanCount(UUID guildId) {
    return guildRepository.getOrThrow(guildId).getBannedUserIds().size();
  }
}
