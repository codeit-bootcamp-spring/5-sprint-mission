package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AddMessageRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message getMessageById(UUID messageId);

    List<Message> getAllMessage();

    Message updateMessage(UUID messageId, UpdateMessageRequest updateMessageRequest);

    void deleteMessage(UUID messageId);

    void deleteAllMessage();

    Message addMessage(AddMessageRequest addMessageRequest);

    List<Message> getAllByChannelId(UUID channelId);
}