package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message sendMessage(String content) {
        if (content.isBlank()) {
            Message message = new Message();
            message.updateContent(content);
            data.put(message.getId(), message);
            return message;
        } else {
            return null;
        }
    }

    @Override
    public Message find(UUID id) {
        if (data.get(id) == null) {
            return null;
        } else {
            return data.get(id);
        }
    }

    @Override
    public List<Message> findAll() {
        List<Message> allMessages = new ArrayList<>();

        for (Message message : data.values()) {
            allMessages.add(message);
        }
        return allMessages;
    }

    @Override
    public Message update(UUID id, String newContent) {
        if (data.get(id) == null || newContent == null || newContent.isBlank()) {
            return null;
        } else {
            Message message = data.get(id);
            message.updateContent(newContent);
            data.put(id, message);
            return message;
        }
    }

    @Override
    public boolean delete(UUID id) {
        return data.remove(id) != null;
    }
}
