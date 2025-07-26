package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final List<Message> data;

    public JCFMessageService() {
        data = new ArrayList<>();
    }

    @Override
    public void createMessage(Message message) {
        data.add(message);
    }

    @Override
    public void updateMessage(Message message) {
        int i = data.indexOf(message);
        data.set(i, message);
    }

    @Override
    public void deleteMessage(Message message) {
        data.remove(message);
    }

    @Override
    public Message searchByIndex(int i) {
        return data.get(i);
    }

    @Override
    public Message searchById(UUID id) {
        for (Message message : data) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> searchByContent(String content) {
        List<Message> messages = new ArrayList<>();
        for (Message message : data) {
            if (message.getContent().contains(content)) {
                messages.add(message);
            }
        }
        return messages;
    }

    @Override
    public List<Message> searchBySenderId(UUID id) {
        List<Message> messages = new ArrayList<>();

        for (Message message : data) {
            if (message.getSender().equals(id)) {
                messages.add(message);
            }
        }

        return messages;
    }

    @Override
    public List<Message> getAllMessages() {
        return data;
    }

    @Override
    public String toString() {
        return "JCFMessageService{" +
                "data=" + data +
                '}';
    }
}
