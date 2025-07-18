package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data = new HashMap<>();

    public void create(Message message) {
        data.put(message.getId(), message);
    }

    public Message read(UUID id) {
        return data.get(id);
    }

    public List<Message> readAll() {
        return new ArrayList<>(data.values());
    }

    public void update(UUID id, String content) {
        Message message = data.get(id);
        if (message != null) message.update(content);
    }

    public void delete(UUID id) {
        data.remove(id);
    }
}
