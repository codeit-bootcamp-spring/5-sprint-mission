package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID id, Message updatedMessage) {
        if (!data.containsKey(id)) return null;
        data.put(id, updatedMessage);
        return updatedMessage;
    }

    @Override
    public Message update(UUID id, String newContent) {
        Message original = data.get(id);
        if (original == null) return null;
        Message updated = original.withContent(newContent);
        data.put(id, updated);
        return updated;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
