package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);

  @Query("SELECT m FROM Message m JOIN FETCH m.author LEFT JOIN FETCH m.attachments WHERE m.channel.id = :channelId")
  List<Message> findAllByChannelIdWithAuthorAndAttachments(@Param("channelId") UUID channelId);

  void deleteAllByChannelId(UUID channelId);

  Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  List<Message> findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(UUID channelId,
      java.time.Instant cursor,
      Pageable pageable);

  List<Message> findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(UUID channelId, Instant cursor, PageRequest of);
}
