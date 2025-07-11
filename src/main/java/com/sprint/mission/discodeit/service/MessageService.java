package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void createMessage(Message message);
    Message getMessage(UUID id);
    List<Message> getAllMessages();
    void updateMessage(Message message);
    void deleteMessage(UUID id);

}
