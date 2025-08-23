package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.Channel;
import java.util.UUID;

public interface ChannelRepository extends AbstractRepository<Channel> {

  boolean existsBetween(UUID userId1, UUID userId2);
}
