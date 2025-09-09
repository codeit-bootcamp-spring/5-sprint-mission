package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  @Query("select m.createdAt "
      + "from Message m "
      + "where m.channel.id = :channelId "
      + "order by m.createdAt desc limit 1")
  Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);

  void deleteAllByChannelId(UUID channelId);
}
