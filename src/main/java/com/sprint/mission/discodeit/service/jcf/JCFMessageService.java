package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> messages = new ArrayList<>();

    @Override
    public Message createMessage(String name, String title, String content) {
        Message message = new Message(name, title, content);
        messages.add(message);
        return message;
    }

    @Override
    public Message readMessage(UUID id) {
        for (Message message : messages) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> readAllMessages() {
        if (!messages.isEmpty()) {
            return messages;
        }
        return null;
    }

    @Override
    public Message updateName(UUID id, String name) {
        for (Message message : messages) {
            if (message.getId().equals(id)) {
                message.setName(name);
                message.update();
                return message;
            }
        }
        return null;
    }

    @Override
    public Message updateTitle(UUID id, String title) {
        for (Message message : messages) {
            if (message.getId().equals(id)) {
                message.setTitle(title);
                message.update();
                return message;
            }
        }
        return null;
    }

    @Override
    public Message updateContent(UUID id, String content) {
        for (Message message : messages) {
            if (message.getId().equals(id)) {
                message.setContent(content);
                message.update();
                return message;
            }
        }
        return null;
    }

    @Override
    public boolean deleteMessage(UUID id) {
        for (Message message : messages) {
            if (message.getId().equals(id)) {
                messages.remove(message);
                return true;
            }
        }
        return false;
    }
}
