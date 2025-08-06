package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository repo;

    public BasicMessageService(MessageRepository repo) {
        this.repo = repo;
    }

    @Override
    public Message createMessage(String content, UUID channelId, UUID authorId) {
        Message message = new Message(content, channelId, authorId);
        return repo.save(message);
    }

    @Override
    public Optional<Message> getMessage(UUID messageId) {
        Optional<Message> message = repo.findById(messageId);
        if(message.isEmpty()){
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }
        return message;
    }

    @Override
    public List<Message> getAllMessages() {
        return repo.findAll();
    }

    @Override
    public Message updateMessage(UUID messageId, String content) {
        Message message = repo.findById(messageId).orElse(null);
        message.update(content);
        return repo.save(message);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        repo.delete(messageId);
    }

    @Override
    public boolean existsById(UUID id) {
        return repo.existById(id);
    }
}
