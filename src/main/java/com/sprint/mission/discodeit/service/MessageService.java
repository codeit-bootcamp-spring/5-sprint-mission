package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void add(Message message);
    Message findOne(UUID messageId);
    List<Message> findAll();
    void update(UUID originMessageUuid , Message newMessage);
    void delete(UUID messageId);
    void deleteAll();
}
