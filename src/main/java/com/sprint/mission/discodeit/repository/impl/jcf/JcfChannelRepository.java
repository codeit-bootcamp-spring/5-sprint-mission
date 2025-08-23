package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class JcfChannelRepository extends AbstractJcfRepository<Channel> implements
    ChannelRepository {

  public JcfChannelRepository() {
    super(Channel.class);
  }

  @Override
  public boolean existsBetween(UUID userId1, UUID userId2) {
    if (userId1 == null || userId2 == null || userId1.equals(userId2)) {
      return false;
    }

    Set<UUID> pair = Set.of(userId1, userId2);

    return data.values().stream()
        .filter(Objects::nonNull)
        .anyMatch(c ->
            c.isNotDeleted()
                && c.getType() == ChannelType.PRIVATE
                && c.getParticipantIds().size() == 2
                && c.getParticipantIds().containsAll(pair)
        );
  }
}
