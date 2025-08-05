package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data = new HashMap<>();

    public JCFMessageRepository() {}

    @Override
    public Optional<Message> save(Message message) {
        if(message == null){
            return Optional.empty();
        }

        data.put(message.getId(), message);
        return Optional.of(message);
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        if(data.containsKey(messageId)){
            return Optional.of(data.get(messageId));
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID messageId) {
        data.remove(messageId);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }
}
