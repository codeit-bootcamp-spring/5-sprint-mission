package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void addMessage(Message Message);
    List<Message> getMessages();
    Message getMessageById(UUID messageId);
    void updateMessage(Message Message, UUID id);
    void deleteMessage(UUID messageId);
    void deleteAll();

}
