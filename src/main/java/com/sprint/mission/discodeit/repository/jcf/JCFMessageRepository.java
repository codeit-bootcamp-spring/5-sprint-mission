package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        data = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        return data.put(message.getId(), message);
    }

    @Override
    public Optional<Message> delete(UUID id) {
        return Optional.ofNullable(data.remove(id));
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public Optional<Message> searchById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> searchByContent(String content) {
        List<Message> messages = new ArrayList<>();
        for (Message message : data.values()) {
            if (message.getContent().contains(content)) {
                messages.add(message);
            }
        }
        return messages;
    }

    @Override
    public List<Message> searchBySenderId(UUID id) {
        List<Message> messages = new ArrayList<>();
        for (Message message : data.values()) {
            if (message.getSenderId().equals(id)) {
                messages.add(message);
            }
        }
        return messages;
    }

    @Override
    public List<Message> searchAll() {
        return new ArrayList<>(data.values());
    }
}
