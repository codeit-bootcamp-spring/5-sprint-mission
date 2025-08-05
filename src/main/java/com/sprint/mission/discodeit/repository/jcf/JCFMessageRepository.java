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
        message = data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        if(data.containsKey(id)){
            return Optional.of(data.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public long count() {
        return data.size();
    }

    @Override
    public Message delete(UUID id) {
        if(!data.containsKey(id)){
            throw new NoSuchElementException("Message with id " + id + " not found");
        }
        return data.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }
}
