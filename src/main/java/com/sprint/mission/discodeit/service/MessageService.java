package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(Message message);
    Message getMessageById(UUID id);
    List<Message> getAllMessages();
    Message updateMessage(Message message);
    void deleteMessage(UUID id);
}
