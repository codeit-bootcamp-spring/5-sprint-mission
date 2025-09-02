package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        this.data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<Message> findLatestByChannelId(UUID channelId) {
        return this.findAllByChannelId(channelId).stream()
                .max(Comparator.comparing(Message::getCreatedAt));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        this.data.remove(id);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        this.findAllByChannelId(channelId).stream()
                .map(Message::getId)
                .toList()
                .forEach(this::delete);
    }

    public void deleteAll() {
        data.clear();
    }
}
