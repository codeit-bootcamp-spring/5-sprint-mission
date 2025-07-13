package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    public boolean addMessage(Message Message);
    public List<Message> getMessages();
    public Message getMessageById(UUID messageId);
    public Message updateMessage(Message Message, UUID id);
    public Message deleteMessage(UUID messageId);
    public void deleteAll();

}
