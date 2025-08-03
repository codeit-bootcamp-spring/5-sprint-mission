package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {


    private final MessageRepository repository;

    public FileMessageService(MessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(Message message) {
        repository.save(message);
    }

    @Override
    public Message findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(Message message) {
        repository.update(message);
    }

    @Override
    public void delete(Message message) {
        repository.delete(message.getId());
    }
}
