package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final JCFMessageRepository messageRepository;

    public JCFMessageService(JCFMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message create(Message message) {

        if (message == null) {
            return null;
        }

        return messageRepository.save(message);
    }

    @Override
    public List<Message> getAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message get(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public Message update(UUID id, String text) {
        Message message = messageRepository.findById(id);

        if (message == null) {
            return null;
        }

        message.update(text);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = messageRepository.findById(id);

        if (message != null) {
            messageRepository.deleteById(id);
        }
    }

    @Override
    public void deleteAll() {
        messageRepository.deleteAll();
    }
}
