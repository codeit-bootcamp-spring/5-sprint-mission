package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    public Message createMessage(UUID userId, UUID channelId, String content);
    public Message readByIdMessage(UUID message);
    public void readAllMessage();
    public void updateMessage(UUID messageUUID, String content);
    public void deleteByIdMessage(UUID message);
}
