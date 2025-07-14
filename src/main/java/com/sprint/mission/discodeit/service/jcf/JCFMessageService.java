package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private static final JCFMessageService instance = new JCFMessageService();
    private final Map<UUID, Message> messages = new HashMap<>();

    private JCFMessageService() {

    }

    public static JCFMessageService getInstnce() { return instance; }

    @Override
    public void create(Message message) {
        messages.putIfAbsent(message.getId(), message);
    }

    @Override
    public Message findById(UUID id) {
        return messages.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();
        for (Message message : messages.values()) {
            if (message.getChannelId().equals(channelId)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public void writeContent(UUID id, Message message) {
        Message msg = messages.get(id);
        if (message != null) {
            message.writeToContent(message.getContent());
        }
    }

    @Override
    public void delete(UUID id) {
        messages.remove(id);
    }
}
