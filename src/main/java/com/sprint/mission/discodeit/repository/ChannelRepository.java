package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  List<Channel> findByType(ChannelType type);

  @EntityGraph(attributePaths = {"messages", "readStatuses", "readStatuses.user"})
  Optional<Channel> findById(UUID id);

  @EntityGraph(attributePaths = {"messages", "readStatuses", "readStatuses.user"})
  List<Channel> findAll();
}
