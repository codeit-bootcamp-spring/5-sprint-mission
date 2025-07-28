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
        if (userRepository.findById(message.getUserId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다: " + message.getUserId());
        }

        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public boolean update(UUID id, Message updatedMessage) {
        Message original = data.get(id);
        if (original != null) {
            original.updateContent(updatedMessage.getContent());
        }
        return false;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}


