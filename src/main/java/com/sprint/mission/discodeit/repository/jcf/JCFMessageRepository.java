package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.IOException;
import java.util.*;

public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public void save(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    @Override
    public Message findByContent(String content) {
        if (content == null || content.isBlank()) return null;
        for (Message message : data.values()) {
            if (message.getContent().equals(content)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(Message message) {
        if (data.containsKey(message.getId())) {
            data.put(message.getId(), message);
        }
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
