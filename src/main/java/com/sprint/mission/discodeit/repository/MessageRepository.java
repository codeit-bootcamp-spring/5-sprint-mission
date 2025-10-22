package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Slice<Message> findAllByChannelIdOrderByCreatedAtDescIdDesc(UUID channelId, Pageable pageable);

  @Query("""
        select m from Message m
        where m.channel.id = :channelId
          and (
            m.createdAt < :createdAt
            or (m.createdAt = :createdAt and m.id < :id)
          )
        order by m.createdAt desc, m.id desc
      """)
  Slice<Message> findNextPage(
      @Param("channelId") UUID channelId,
      @Param("createdAt") Instant createdAt,
      @Param("id") UUID id,
      Pageable pageable
  );

  @EntityGraph(attributePaths = {"channel"})
  Optional<Message> findTopByChannelIdOrderByCreatedAtDescIdDesc(UUID channelId);
}
