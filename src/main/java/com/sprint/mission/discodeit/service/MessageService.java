package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;

public interface MessageService {
    void createMessage(String content, String channelName, String creatorUserId);
    Message findMessage(String content);
    List<Message> findAllMessages();
    void updateMessage(String content, String newContent);
    void deleteMessage(String content);
}
