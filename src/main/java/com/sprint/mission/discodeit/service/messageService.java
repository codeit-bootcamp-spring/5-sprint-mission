package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface messageService {

    void createMessage(Message message);

    Message getMessageById(UUID messageId);

    List<Message> getAllMessages();

    void updateMessage(UUID messageId, Message message);

    void updateMessageUpdatedAt(UUID messageId, long updatedAt);

    void deleteMessage(UUID messageId);

}
