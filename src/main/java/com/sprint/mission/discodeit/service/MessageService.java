package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message addMessage(String messageContent, UUID channelId, UUID userId);
    Message getMessageById(UUID messageId);
    List<Message> getAllMessage();
    Message updateMessage(UUID messageId, String messageContent);
    void deleteMessage(UUID messageId);
    void deleteAllMessage();


}
