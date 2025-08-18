package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.Channel;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ChannelRepository extends AbstractRepository<Channel> {

  List<Channel> findAllByGuildId(UUID guildId);

  List<Channel> findAllPublicByGuildId(UUID guildId);

  List<Channel> findAllSecretByGuildIdAndMember(UUID guildId, UUID memberId);

  boolean existsDmByMembers(Set<UUID> members);

  Optional<Channel> findDmByMembers(Set<UUID> members);

  boolean isMember(UUID channelId, UUID userId);

  List<Channel> findAllByGuildIdIn(Collection<UUID> guildIds);
}
