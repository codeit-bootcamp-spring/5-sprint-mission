package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JFCMessageService implements MessageService {

    Map<UUID, Message> messages = new HashMap<UUID, Message>();

    @Override
    public void create(Message message) {
        messages.put(message.getId(), message);
    }

    @Override
    public Message findById(UUID id) {
        if (messages.containsKey(id)){
            return messages.get(id);
        }

        return null;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public void update(UUID id, String content) {
        Message message = messages.get(id);
        if (message != null) {
            message.update(content);
        }
    }

    @Override
    public void delete(UUID id) {
        messages.remove(id);
    }
}
