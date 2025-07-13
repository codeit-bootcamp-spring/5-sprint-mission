package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;

public interface MessageService {
    void addMessage(Message message);

    void updateMessage(Message message);

    void deleteMessage(Message message);

    Message getMessage(int i);

    List<Message> getAllMessages();
}
