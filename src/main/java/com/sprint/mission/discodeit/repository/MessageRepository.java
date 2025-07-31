package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Message findById(UUID id);
    List<Message> findAll();
    Message update(UUID id, Message updatedMessage);
    Message update(UUID id, String newContent);
    void delete(UUID id);
}

