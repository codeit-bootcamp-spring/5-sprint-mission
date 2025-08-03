package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messageMap;

    public JCFMessageRepository() {
        messageMap = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        messageMap.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        if (messageMap.containsKey(messageId)) {
            return Optional.of(messageMap.get(messageId));
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public boolean delete(UUID messageId) {
        return messageMap.remove(messageId, messageMap.get(messageId));
    }
}
