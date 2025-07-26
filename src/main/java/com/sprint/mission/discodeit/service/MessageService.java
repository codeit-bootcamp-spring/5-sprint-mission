package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message create(Message message);

    Message update(Message message);

    Message delete(UUID id);

    void deleteAll();

    Optional<Message> searchById(UUID id);

    List<Message> searchByContent(String content);

    List<Message> searchBySenderId(UUID id);

    List<Message> searchAll();
}
