package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final MessageRepository messageRepository;

    public FileMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        Message message = new Message(channelId, authorId, content);
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found"));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = find(messageId);
        message.update(content);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        messageRepository.deleteById(messageId);
    }

    @Override
    public void clear() {
        messageRepository.clear();
    }
}