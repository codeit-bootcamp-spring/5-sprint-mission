package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(Message message);

    Message updateContent(UUID id, String content);

    Message updateSenderId(UUID id, UUID senderId);

    Message updateChannelId(UUID id, UUID channelId);

    Message delete(UUID id);

    void deleteAll();

    Message searchById(UUID id);

    List<Message> searchByContent(String content);

    List<Message> searchBySenderId(UUID id);

    List<Message> searchAll();
}
