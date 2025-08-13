package com.codeit.mission.discodeit.repository.jcf;

import com.codeit.mission.discodeit.entity.Message;
import com.codeit.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

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
    public List<Message> findAllByChannelId(UUID channelId) {
        return this.data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId)).toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        this.findAllByChannelId(channelId)
                .forEach(message -> this.deleteById(message.getId()));
    }
}
