package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Message save(Message message);

  Optional<Message> findById(UUID id);

  List<Message> findAllByChannelId(UUID channelId);

  boolean existsById(UUID id);

  void deleteById(UUID id);

  void deleteAllByChannelId(UUID channelId);

  // cursor 없을 때 (최신 메시지부터)
  List<Message> findTop51ByChannelIdOrderByCreatedAtDesc(UUID channelId);

  // cursor 있을 때 (cursor 이전 메시지)
  List<Message> findTop51ByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(UUID channelId, LocalDateTime cursor);
}
