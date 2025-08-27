package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messages = new HashMap<>();

    @Override
    public Message save(Message message) {
        messages.put(message.getId(), message);
        System.out.println("[Repo]Message saved to JCF cache: " + message.getId());
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        System.out.println("[Repo] Finding message by Id in JCF cache: " + id);
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public List<Message> findAll() {
        System.out.println("[Repo] Finding all messages in JCF cache: " + messages.size());
        return new ArrayList<>(messages.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        System.out.println("[Repo] Finding all messages in JCF cache: " + channelId);
        return messages.values().stream()
                .filter(m -> m.getChannelId().equals(channelId)).collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        if (messages.remove(id) == null) {
            System.out.println("[Repo]Message deleted for JCF cache: " + id);
        } else {
            throw new RuntimeException("[Repo]Message not found in JCF cache: " + id);
        }
    }
}
