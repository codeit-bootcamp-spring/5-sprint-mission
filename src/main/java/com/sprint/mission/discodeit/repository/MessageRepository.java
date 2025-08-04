package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);

    Optional<Message> delete(UUID id);

    void deleteAll();

    Optional<Message> searchById(UUID id);

    List<Message> searchByContent(String content);

    List<Message> searchBySenderId(UUID id);

    List<Message> searchAll();
}
