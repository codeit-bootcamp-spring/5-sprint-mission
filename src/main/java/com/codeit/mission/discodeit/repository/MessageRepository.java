package com.codeit.mission.discodeit.repository;

import com.codeit.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
