package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JCFMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("Message content cannot be null or blank.");
        }
        if (!userRepository.existsById(request.getAuthorId())) {
            throw new IllegalArgumentException("User not found with id: " + request.getAuthorId());
        }
        if (!channelRepository.existsById(request.getChannelId())) {
            throw new IllegalArgumentException("Channel not found with id: " + request.getChannelId());
        }

        Message message = new Message(
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                request.getContent(),
                request.getChannelId(),
                request.getAuthorId(),
                request.getAttachmentIds()
        );
        Message savedMessage = messageRepository.save(message);
        return toMessageResponse(savedMessage);
    }

    @Override
    public MessageResponse find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found with id: " + messageId));
        return toMessageResponse(message);
    }

    @Override
    public List<MessageResponse> findAll() {
        return messageRepository.findAll().stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(this::toMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("Message not found with id: " + request.getId()));

        if (request.getContent() != null && !request.getContent().isBlank()) {
            message.setContent(request.getContent());
        }
        message.setUpdatedAt(Instant.now());
        Message updatedMessage = messageRepository.save(message);
        return toMessageResponse(updatedMessage);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message not found with id: " + messageId);
        }
        messageRepository.findById(messageId).ifPresent(message -> {
            message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
        });
        messageRepository.deleteById(messageId);
    }

    @Override
    public void clear() {
        messageRepository.clear();
    }

    private MessageResponse toMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getAttachmentIds()
        );
    }
}
