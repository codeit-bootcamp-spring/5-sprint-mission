package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> messages = new ArrayList<>();

    @Override
    public boolean register(Message message) {
        if (message.getUserId() == null || message.getChannelId() == null || message.getContent() == null || message.getContent().isBlank()) {
            return false;
        }
        messages.add(message);
        return true;
    }

    @Override
    public Message findById(UUID id) {
        for (Message message : messages) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(messages);
    }

    @Override
    public boolean update(UUID id, String newContent) {
        for (Message message : messages) {
            if (message.getId().equals(id)) {
                message.setContent(newContent);
                message.setUpdateAt(System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(UUID id) {
        for (Message message : messages) {
            if (message.getId().equals(id)) {
                messages.remove(message);
                return true;
            }
        }
        return false;
    }
}
