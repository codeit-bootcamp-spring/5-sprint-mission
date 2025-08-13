package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messageMap = new HashMap<>();

    @Override
    public void save(Message message) {
        messageMap.put(message.getId(), message);
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(messageMap.get(messageId));
    }

    @Override
    public List<Message> findByUser(User user) {
        return messageMap.values().stream()
                .filter(message -> message.getUser().equals(user))
                .toList();
    }

    @Override
    public List<Message> findByMessage(String message) {
        return messageMap.values().stream()
                .filter(m -> m.getContent().contains(message))
                .toList();
    }

    @Override
    public List<Message> findAll() {
        return messageMap.values().stream().toList();
    }

    @Override
    public boolean delete(UUID id) {
        return messageMap.remove(id) != null;
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        messageMap.values().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList()
                .forEach(message -> messageMap.remove(message.getId()));
    }
}
