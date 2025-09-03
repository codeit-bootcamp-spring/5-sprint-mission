package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Slice<Message> findByChannelId(UUID channelId, Pageable pageable);

  List<Message> findByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

  List<Message> findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(UUID channelId,
      Instant createdAt, Pageable pageable);

  Long countByChannelId(UUID channelId);
}
