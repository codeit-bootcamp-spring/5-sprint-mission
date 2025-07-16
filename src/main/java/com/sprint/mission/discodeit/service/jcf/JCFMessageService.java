package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public final class JCFMessageService implements MessageService {
    private static final JCFMessageService instance = new JCFMessageService();
    private final Map<UUID, Message> data;

    private JCFMessageService() {
        this.data = new HashMap<>();
    }

    public static JCFMessageService getInstance() {
        return instance;
    }

    @Override
    public void create(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public Message get(UUID id) {
        return data.get(id);
    }

    @Override
    public Message get(String content) {
        if (content == null || content.isBlank()) return null;
        for (Message message : data.values()) {
            if (message.getContent().equals(content)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> getAll() {
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
