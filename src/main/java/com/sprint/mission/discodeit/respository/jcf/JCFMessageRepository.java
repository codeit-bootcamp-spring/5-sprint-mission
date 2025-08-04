package com.sprint.mission.discodeit.respository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.respository.MessageRepository;
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
    public List<Message> findByStr(String str) {
        List<Message> result = new ArrayList<>();
        for (Message message : data.values()) {
            if (message.getContent().contains(str)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public boolean deleteById(UUID id) {
        return data.remove(id) != null;
    }
}
