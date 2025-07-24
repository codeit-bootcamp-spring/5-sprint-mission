package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    MessageRepository repo;

    public BasicMessageService(MessageRepository repo) {
        this.repo = repo;
    }

    @Override
    public void create(Message message) {
        repo.save(message);
    }

    @Override
    public void update(Message message) {
        repo.delete(message);
        repo.save(message);
    }

    @Override
    public void delete(Message message) {
        repo.delete(message);
    }

    @Override
    public void deleteAll() {
        repo.deleteAll();
    }

    @Override
    public Message searchById(UUID id) {
        return repo.searchById(id);
    }

    @Override
    public List<Message> searchByContent(String content) {
        return repo.searchByContent(content);
    }

    @Override
    public List<Message> searchBySenderId(UUID id) {
        return repo.searchBySenderId(id);
    }

    @Override
    public List<Message> searchAll() {
        return repo.searchAll();
    }
}
