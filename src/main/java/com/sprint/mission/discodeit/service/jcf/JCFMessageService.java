package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message createMessage(String content) {
        Message message = new Message(content);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message getMessage(UUID messageId) {
        if(!data.containsKey(messageId)){
            throw new NoSuchElementException("message not found");
        }
        return data.get(messageId);
    }

    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message updateMessage(UUID messageId, String content) {
        Message message = getMessage(messageId);
        message.update(messageId, content);
        return message;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        if(!data.containsKey(messageId)){
            throw new NoSuchElementException("message not found");
        }
        data.remove(messageId);
    }
}
