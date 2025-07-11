package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final HashMap<UUID, Message> data;

    public JCFMessageService(HashMap<UUID, Message> data) {
        this.data = data;
    }

    @Override
    public void addMessage(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public void updateMessage(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public void deleteMessage(UUID id) {
        data.remove(id);
    }
    @Override
    public Message getMessage(UUID id) {
        return data.get(id);
    }

    @Override
    public HashMap<UUID, Message> getAllMessages() {
        return data;
    }
}
