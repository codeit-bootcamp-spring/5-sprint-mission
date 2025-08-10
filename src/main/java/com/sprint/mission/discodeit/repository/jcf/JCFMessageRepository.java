package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

<<<<<<< HEAD
    public  JCFMessageRepository() {
=======
    public JCFMessageRepository() {
>>>>>>> 717adae (feat: 초기 커밋)
        this.data = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        this.data.put(message.getId(), message);
        return message;
    }

    @Override
<<<<<<< HEAD
    public Optional<Message> find(UUID messageId) {
        return Optional.ofNullable(this.data.get(messageId));
=======
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
>>>>>>> 717adae (feat: 초기 커밋)
    }

    @Override
    public List<Message> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
<<<<<<< HEAD
    public boolean existById(UUID messageId) {
        return data.containsKey(messageId);
    }

    @Override
    public void delete(UUID messageId) {
        if (!this.data.containsKey(messageId)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }
        this.data.remove(messageId);
=======
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
>>>>>>> 717adae (feat: 초기 커밋)
    }
}
