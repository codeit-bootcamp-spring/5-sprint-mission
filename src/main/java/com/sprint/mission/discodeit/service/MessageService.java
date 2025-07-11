package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    void addMessage(Message message);
    void updateMessage(Message message);
    void deleteMessage(UUID id);
    Message getMessage(UUID id);
    HashMap<UUID, Message> getAllMessages();

}
