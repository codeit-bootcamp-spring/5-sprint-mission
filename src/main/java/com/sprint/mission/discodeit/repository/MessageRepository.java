package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    void save(Message message);

    void delete(Message message);

    void deleteAll();

    Message searchById(UUID id);

    List<Message> searchByContent(String content);

    List<Message> searchBySenderId(UUID id);

    List<Message> searchAll();
}
