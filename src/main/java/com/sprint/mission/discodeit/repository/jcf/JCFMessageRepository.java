package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageRepository implements MessageRepository {

    private final List<Message> messages;

    public JCFMessageRepository() {
        this.messages = new ArrayList<>();
    }

    @Override
    public void save(Message message) {
        messages.add(message);
    }

    @Override
    public List<Message> findByUser(User user) {
        return messages.stream()
                .filter(message -> message.getUser().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByMessage(String message) {
        return messages.stream()
                .filter(m -> m.getMessage().contains(message))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findAll() {
        return messages;
    }

    @Override
    public void update(UUID id, Message message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
