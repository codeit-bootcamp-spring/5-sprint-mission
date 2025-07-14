package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messages;

    public JCFMessageService() {
        messages = new HashMap<>();
    }

    @Override
    public Message createMessage(String content, String userId, UUID channelId) {
        Message message = new Message(content, userId, channelId);
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public String getMessageById(UUID id) {
        for (Message message : messages.values()) {
            if (message.getId().equals(id)) {
                return message.getContent();
            }
        }
        return null;
    }

    @Override
    public List<Message> getMessages() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        List<Message> list = new ArrayList<>();
       for (Message message : messages.values()) {
           if (message.getChannelId().equals(channelId)) {
               list.add(message);
           }
       }
       return list;
    }

    @Override
    public List<Message> getMessagesByUser(String userId) {
        List<Message> list = new ArrayList<>();
        for (Message message : messages.values()) {
            if (message.getUserId().equals(userId)) {
                list.add(message);
            }
        }
        return list;
    }

    @Override
    public boolean updateMessage(UUID messageId, String nContent) {
        Message message = messages.get(messageId);
        if (message != null) {
            message.setContent(nContent);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteMessage(UUID messageId) {
        Message message = messages.get(messageId);
        if (message != null) {
            messages.remove(messageId);
            return true;
        }
        return false;
    }
}
