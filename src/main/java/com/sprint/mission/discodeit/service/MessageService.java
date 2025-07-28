package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void create(Message message);

    Message get(UUID id);

    Message get(String content);

    List<Message> getAll();

    void update(Message message);

    void delete(UUID id);
}
