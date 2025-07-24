package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private static final Map<UUID, Message> data = new HashMap<>();
    private static JCFMessageService instance;

    private JCFMessageService() {}

    public static JCFMessageService getInstance() {
        if (instance == null) {
            instance = new JCFMessageService();
        }
        return instance;
    }


    @Override
    public void add(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public Message findOne(UUID messageId) {
        return data.get(messageId);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID originMessageUuid, Message newMessage) {
        Message existingMessage = data.remove(originMessageUuid);

        existingMessage.updateContent(newMessage.getContent());

        data.put(existingMessage.getId(), existingMessage);
    }

    @Override
    public void delete(UUID messageId) {
        data.remove(messageId);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }
}
