package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface messageService {

    void createMessage(Message message);

    User getMessageById(UUID messageId);

    List<User> getAllMessages();

    void updateMessage(UUID messageId, Message message);

    void deleteMessage(UUID messageId);

}
