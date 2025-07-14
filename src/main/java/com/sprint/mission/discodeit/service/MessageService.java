package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void createMessage(Message message);

    void updateMessage(Message message);

    void deleteMessage(Message message);

    Message searchByIndex(int i);

    Message searchById(UUID id);

    List<Message> searchByContent(String content);

    List<Message> getAllMessages();
}
