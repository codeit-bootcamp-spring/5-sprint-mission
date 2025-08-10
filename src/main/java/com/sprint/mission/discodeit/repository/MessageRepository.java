package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
<<<<<<< HEAD
    Optional<Message> find(UUID messageId);
    List<Message> findAll();
    boolean existById(UUID messageId);
    void delete(UUID messageId);
=======
    Optional<Message> findById(UUID id);
    List<Message> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
>>>>>>> 717adae (feat: 초기 커밋)
}
