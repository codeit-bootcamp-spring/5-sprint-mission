package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void createMessage(String content, String channelName, String creatorUserId);
    Message findMessage(UUID msgId);
    List<Message> findAllMessages();
    void updateMessage(UUID msgId, String newMsg);
    void deleteMessage(UUID msgId);
}
