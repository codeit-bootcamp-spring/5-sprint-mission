package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

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
    public Message update(UUID id, Message msgDto) {
        validate(msgDto);
        Message msg = findById(id);
        msg.editContent(msgDto.getContent());
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
