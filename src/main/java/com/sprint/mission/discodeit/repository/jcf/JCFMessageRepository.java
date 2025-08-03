package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> userMap;

    public JCFMessageRepository() {
        userMap = new HashMap<>();
    }

    @Override
    public Message save(Message user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<Message> findById(UUID userId) {
        if (userMap.containsKey(userId)) {
            return Optional.of(userMap.get(userId));
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public boolean delete(UUID userId) {
        return userMap.remove(userId, userMap.get(userId));
    }
}
