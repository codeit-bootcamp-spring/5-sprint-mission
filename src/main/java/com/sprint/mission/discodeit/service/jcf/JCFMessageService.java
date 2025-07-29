package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        if (content == null || channelId == null || authorId == null || content.isBlank()){
            throw new IllegalArgumentException("message content or channelId or authorId is null or blank");
        }

        Message message = new Message(content, channelId, authorId);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message find(UUID messageId) {
        if (!data.containsKey(messageId)){
            throw new NoSuchElementException("message not found");
        }
        return data.get(messageId);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID messageId, String content, UUID channelId, UUID authorId) {
        Message message = data.get(messageId);

        if (message == null) {
            throw new NoSuchElementException("message not found");
        }

        message.update(content, channelId, authorId);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        data.remove(messageId);
    }
}
