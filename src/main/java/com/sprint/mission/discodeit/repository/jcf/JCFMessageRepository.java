package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> storage = new HashMap<>();

    @Override
    public Message save(Message message) {
        storage.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }

    @Override
    public boolean deleteById(UUID id) {
        return storage.remove(id) != null;
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return storage.values().stream()
                .filter(msg -> msg.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public Instant findRecentMessageTimeByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    @Override
    public boolean deleteAllByChannelId(UUID channelId) {
        int originalSize = storage.size();
        storage.values().removeIf(msg -> msg.getChannelId().equals(channelId));
        return storage.size() < originalSize;
    }
}
