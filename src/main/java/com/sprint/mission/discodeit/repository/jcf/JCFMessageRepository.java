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
            throw new IllegalArgumentException("message 파라미터가 null 입니다.");
        }

        data.put(message.getId(), message);
        return Optional.of(message);
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        if(data.containsKey(messageId)){
            return Optional.of(data.get(messageId));
        }
        throw new IllegalArgumentException("존재하지 않는 메시지 입니다.");
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(Message message) {
        UUID id = message.getId();
        data.remove(id);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }
}
