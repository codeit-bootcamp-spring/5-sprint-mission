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
    public Message save(Message messageDto) {
        validate(messageDto);
        return messageRepository.save(messageDto);
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message not found: " + id));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID id, Message messageDto) {
        validate(messageDto);
        Message msg = findById(id);
        msg.editContent(messageDto.getContent());
        return messageRepository.save(msg);
    }

    @Override
    public void delete(UUID id) {
        Message msg = findById(id);
        messageRepository.delete(msg.getId());
    }

    @Override
    public void deleteAll() {
        messageRepository.deleteAll();
    }

    private void validate(Message msgDto) {
        if (msgDto == null) {
            throw new IllegalArgumentException("Message must not be null");
        }
        if (msgDto.getChannelId() == null) {
            throw new IllegalArgumentException("Message channel is required");
        }
        if (msgDto.getAuthorId() == null) {
            throw new IllegalArgumentException("Message author is required");
        }
        if (msgDto.getContent() == null || msgDto.getContent().isBlank()) {
            throw new IllegalArgumentException("Message content is required");
        }
    }
}
