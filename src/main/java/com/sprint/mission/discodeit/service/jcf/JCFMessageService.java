package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data = new HashMap<>();
    private final UserRepository userRepository;

    public JCFMessageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Message create(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message read(UUID id) {
        return data.get(id);
    }

    @Override
    public boolean update(UUID id, String newContent) {
        Message old = data.get(id);
        if (old == null) return false;
        data.put(id, old.withContent(newContent));
        return true;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(data.values());
    }
}

