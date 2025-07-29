package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> storage = new HashMap<>();

    @Override
    public Message save(Message message) {
        message.updateTimestamp();               // 수정 시간 갱신
        storage.put(message.getId(), message);   // UUID 기반 저장
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return storage.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }
}
