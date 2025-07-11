package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public Message find(UUID id) {
        return data.get(id);
    }

    @Override
    public ArrayList<Message> allFind() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, Message message) {
        if (data.containsKey(id)){
            data.put(id, message);
        }
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
