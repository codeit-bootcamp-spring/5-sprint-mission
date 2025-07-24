package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileMessageService implements MessageService {

    MessageRepository repo;

    public FileMessageService(MessageRepository repo) {
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
        Message message = repo.searchById(id).orElse(null);
        if (message == null) {
            System.err.println("해당하는 메세지를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return message;
    }

    @Override
    public List<Message> searchByContent(String content) {
        List<Message> messages = repo.searchByContent(content);
        if (messages.isEmpty()) {
            System.err.println("해당하는 메세지를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return messages;
    }

    @Override
    public List<Message> searchBySenderId(UUID id) {
        List<Message> messages = repo.searchBySenderId(id);
        if (messages.isEmpty()) {
            System.err.println("해당하는 메세지를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return messages;
    }

    @Override
    public List<Message> searchAll() {
        return repo.searchAll();
    }
}
