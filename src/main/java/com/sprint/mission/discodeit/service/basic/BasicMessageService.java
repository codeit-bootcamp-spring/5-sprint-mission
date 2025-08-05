package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository repository;

    public BasicMessageService(MessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public Message create(Message message) {
        return repository.save(message);
    }

    @Override
    public Message read(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Message> readAll() {
        return repository.findAll();
    }

    @Override
    public boolean update(UUID id, String newContent) {
        Message updated = repository.update(id, newContent);
        return updated != null;
    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }
}
