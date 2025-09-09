package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @EntityGraph(attributePaths = {"author", "channel", "attachments"})
  Optional<Message> findById(UUID id);

  @EntityGraph(attributePaths = {"author", "channel", "attachments"})
  List<Message> findByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

  @EntityGraph(attributePaths = {"author", "channel", "attachments"})
  List<Message> findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(UUID channelId,
      Instant createdAt, Pageable pageable);

  Long countByChannelId(UUID channelId);
}
