package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageRepository implements MessageRepository {

    private Map<UUID, Message> messages = new HashMap<>();

    @Override
    public Message save(Message message) {
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        if (messages.containsKey(id)) {
            return Optional.of(messages.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return messages.values().stream().collect(Collectors.toList());
    }

    @Override
    public Message update(UUID id, Message message) {
        if (!messages.containsKey(id)) {
            throw new NoSuchElementException();
        }
        messages.put(id, message);
        return message;
    }

    @Override
    public boolean existsById(UUID id) {
        if (messages.containsKey(id)) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteById(UUID id) {
        messages.remove(id);
    }

}
