package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageMap;

    public JCFMessageService() {
        messageMap = new HashMap<>();
    }

    @Override
    public Message create(String content, UUID channelId, UUID userId) {
        if (content == null || content.isBlank() || channelId == null || userId == null) {
            throw new IllegalArgumentException("Message info is invalid");
        }
        Message message = new Message(content, channelId, userId);
        messageMap.put(message.getId(), message);
        return message;
    }

    @Override
    public Message find(UUID messageId) {
        Message message = messageMap.get(messageId);
        if (message == null) {
            throw new NoSuchElementException("Message not found");
        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = messageMap.get(messageId);
        if (message == null) {
            throw new NoSuchElementException("Message not found");
        }
        message.update(content);
        return message;
    }

    @Override
    public boolean delete(UUID messageId) {
        return messageMap.remove(messageId, find(messageId));
    }
}
