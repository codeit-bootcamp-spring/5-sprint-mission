package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository {
    Message save(Message message);
    Optional<Instant> findLastMessageAtByChannelId(UUID channelId);
    Optional<Message> findById(UUID id);
    List<Message> findAllByChannelId(UUID channelId);
    List<Message> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
