package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message messageDto);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    Message update(UUID id, Message messageDto);

    void delete(UUID id);
}
