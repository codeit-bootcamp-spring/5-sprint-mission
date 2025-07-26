package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        data = new HashMap<>();
    }

    @Override
    public void save(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public void delete(Message message) {
        if (!data.containsKey(message.getId())) {
            System.err.println("해당하는 메세지를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        data.remove(message.getId());
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public Message searchById(UUID id) {
        if (!data.containsKey(id)) {
            System.err.println("해당하는 메세지를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return data.get(id);
    }

    @Override
    public List<Message> searchByContent(String content) {
        List<Message> messages = new ArrayList<>();
        for (Message message : data.values()) {
            if (message.getContent().contains(content)) {
                messages.add(message);
            }
        }
        if (messages.isEmpty()) {
            System.err.println("해당하는 메세지를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return messages;
    }

    @Override
    public List<Message> searchBySenderId(UUID id) {
        List<Message> messages = new ArrayList<>();
        for (Message message : data.values()) {
            if (message.getSenderId().equals(id)) {
                messages.add(message);
            }
        }
        if (messages.isEmpty()) {
            System.err.println("해당하는 메세지를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return messages;
    }

    @Override
    public List<Message> searchAll() {
        return new ArrayList<>(data.values());
    }
}
