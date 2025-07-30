package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final MessageRepository repo;

    public JCFMessageService(MessageRepository repo) {
        this.repo = repo;
    }

    @Override
    public Message createMessage(String content) {
        Message message = new Message(content);
        return repo.save(message);
    }

    @Override
    public Optional<Message> getMessage(UUID messageId) {
        Optional<Message> message = repo.findById(messageId);
        if(message.isEmpty()){
            throw new NoSuchElementException("User with id " + messageId + " not found");
        }
        return message;
    }

    @Override
    public List<Message> getAllMessages() {
        return repo.findAll();
    }

    @Override
    public Message updateMessage(UUID messageId,String content) {
       return repo.update(messageId, content);
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
