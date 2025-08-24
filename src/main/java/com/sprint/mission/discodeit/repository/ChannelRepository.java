package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelRepository extends AbstractRepository<Channel> {

  List<Channel> findAllPublic();

  boolean existsBetween(UUID userId1, UUID userId2);
}
