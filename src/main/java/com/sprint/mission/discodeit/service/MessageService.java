package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(Message message);

    List<Message> getAll();

    Message get(UUID id);

    Message update(UUID id, String text);

    void delete(UUID id);

    void deleteAll();
}
