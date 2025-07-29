package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private static final Map<UUID, Message> data = new HashMap<>();

    public JCFMessageService() {}

    @Override
    public void add(Message message) {
        if(message == null){
            throw new IllegalArgumentException("message는 null일 수 없다.");
        }
        data.put(message.getId(), message);
    }

    @Override
    public Message findOne(UUID messageId) {
        if(messageId == null){
            throw new IllegalArgumentException("messageId는 null일 수 없다.");
        }

        return data.get(messageId);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID originMessageUuid, Message newMessage) {
        if(originMessageUuid == null || newMessage == null){
            throw new IllegalArgumentException("messageId 혹은 message는 null일 수 없다.");
        }

        Message existingMessage = data.remove(originMessageUuid);

        existingMessage.updateContent(newMessage.getContent());

        data.put(existingMessage.getId(), existingMessage);
    }

    @Override
    public void delete(UUID messageId) {
        if(messageId == null){
            throw new IllegalArgumentException("messageId는 null일 수 없다.");
        }

        data.remove(messageId);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }
}
