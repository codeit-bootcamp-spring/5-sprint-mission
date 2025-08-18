package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class JcfChannelRepository extends AbstractJcfRepository<Channel> implements
    ChannelRepository {

  @Override
  protected String getEntityTypeName() {
    return "채널";
  }

  @Override
  public List<Channel> findAllByGuildId(UUID guildId) {
    Objects.requireNonNull(guildId, "guildId must not be null");
    return findAll().stream()
        .filter(Channel::isGuildChannel)
        .filter(c -> guildId.equals(c.getGuildId()))
        .toList();
  }

  @Override
  public List<Channel> findAllPublicByGuildId(UUID guildId) {
    Objects.requireNonNull(guildId, "guildId must not be null");
    return findAll().stream()
        .filter(Channel::isPublicGuildChannel)
        .filter(c -> guildId.equals(c.getGuildId()))
        .toList();
  }

  @Override
  public List<Channel> findAllSecretByGuildIdAndMember(UUID guildId, UUID memberId) {
    Objects.requireNonNull(guildId, "guildId must not be null");
    Objects.requireNonNull(memberId, "memberId must not be null");
    return findAll().stream()
        .filter(Channel::isSecretGuildChannel)
        .filter(c -> guildId.equals(c.getGuildId()))
        .filter(c -> c.isMember(memberId))
        .toList();
  }

  @Override
  public boolean existsDmByMembers(Set<UUID> members) {
    return findDmByMembers(members).isPresent();
  }

  @Override
  public Optional<Channel> findDmByMembers(Set<UUID> members) {
    Objects.requireNonNull(members, "members must not be null");
    if (members.isEmpty() || members.contains(null)) {
      throw new IllegalArgumentException("members must not be empty and must not contain null");
    }
    Set<UUID> target = new HashSet<>(members);
    return findAll().stream()
        .filter(c -> !c.isGuildChannel())
        .filter(c -> {
          Set<UUID> m = c.getMemberIds();
          return m.size() == target.size() && m.containsAll(target);
        })
        .findFirst();
  }

  @Override
  public boolean isMember(UUID channelId, UUID userId) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    Objects.requireNonNull(userId, "userId must not be null");
    return findById(channelId)
        .map(c -> c.isPublicGuildChannel() || c.isMember(userId))
        .orElse(false);
  }

  @Override
  public List<Channel> findAllByGuildIdIn(Collection<UUID> guildIds) {
    if (guildIds == null || guildIds.isEmpty()) {
      return List.of();
    }
    Set<UUID> set = new HashSet<>(guildIds);
    return findAll().stream()
        .filter(Channel::isGuildChannel)
        .filter(c -> set.contains(c.getGuildId()))
        .toList();
  }
}
