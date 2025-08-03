package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface MessageService {
    Message createMessage(String content, UUID chanelId, UUID authorId);
    Optional<Message> getMessage(UUID messageId);
    List<Message> getAllMessages();
    Message updateMessage(UUID messageId, String content);
    void deleteMessage(UUID messageId);
    boolean existsById(UUID id);
}
