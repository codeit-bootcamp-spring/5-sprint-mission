package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<String, Message> data;

    public JCFMessageService(Map<String, Message> data) {
        this.data = data;
    }

    @Override
    public Message createMessage(Message message) {
        if (message.getId() == null) {
            message.setId(UUID.randomUUID());
        }

        Long currentTime = System.currentTimeMillis();
        message.setCreateAt(currentTime);
        message.setUpdateAt(currentTime);

        data.put(message.getId().toString(), message);
        return message;
    }

    @Override
    public Message getMessageById(UUID id) {
        return data.get(id.toString());
    }

    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message updateMessage(Message message) {
        if (message.getId() == null || !data.containsKey(message.getId().toString())) {
            return null;
        }

        Message existingMessage = data.get(message.getId().toString());
        message.setCreateAt(existingMessage.getCreateAt());
        message.setUpdateAt(System.currentTimeMillis());

        data.put(message.getId().toString(), message);
        return message;
    }

    @Override
    public void deleteMessage(UUID id) {
        data.remove(id.toString());
    }
}
