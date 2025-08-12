package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageDeleteRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("basicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Override
    public MessageResponseDto create(MessageCreateRequest request) {
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + request.channelId());
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("Author not found with id " + request.authorId());
        }

        Message message = new Message(request.content(), request.channelId(), request.authorId());
        Message savedMessage = messageRepository.save(message);

        if (request.attachments() != null && !request.attachments().isEmpty()) {
            for (BinaryContent attachment : request.attachments()) {
                attachment.setMessageId(savedMessage.getId());
                binaryContentRepository.save(attachment);
            }
        }

        List<UUID> attachmentIds = binaryContentRepository.findAllByMessageId(savedMessage.getId())
                .stream()
                .map(BinaryContent::getId)
                .toList();

        return new MessageResponseDto(
                savedMessage.getId(),
                savedMessage.getContent(),
                savedMessage.getChannelId(),
                savedMessage.getAuthorId(),
                attachmentIds,
                savedMessage.getCreatedAt(),
                savedMessage.getUpdatedAt()
        );
    }

    @Override
    public MessageResponseDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        List<UUID> attachmentIds = binaryContentRepository.findAllByMessageId(messageId)
                .stream()
                .map(BinaryContent::getId)
                .toList();

        return new MessageResponseDto(
                message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                attachmentIds,
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found with id " + channelId);
        }

        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(message -> {
                    List<UUID> attachmentIds = binaryContentRepository.findAllByMessageId(message.getId())
                            .stream()
                            .map(BinaryContent::getId)
                            .toList();

                    return new MessageResponseDto(
                            message.getId(),
                            message.getContent(),
                            message.getChannelId(),
                            message.getAuthorId(),
                            attachmentIds,
                            message.getCreatedAt(),
                            message.getUpdatedAt()
                    );
                })
                .toList();
    }

    @Override
    public MessageResponseDto update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + request.messageId() + " not found"));

        message.update(request.newContent());
        Message updatedMessage = messageRepository.save(message);

        List<UUID> attachmentIds = binaryContentRepository.findAllByMessageId(updatedMessage.getId())
                .stream()
                .map(BinaryContent::getId)
                .toList();

        return new MessageResponseDto(
                updatedMessage.getId(),
                updatedMessage.getContent(),
                updatedMessage.getChannelId(),
                updatedMessage.getAuthorId(),
                attachmentIds,
                updatedMessage.getCreatedAt(),
                updatedMessage.getUpdatedAt()
        );
    }

    @Override
    public void delete(MessageDeleteRequest request) {
        UUID messageId = request.messageId();
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }
        binaryContentRepository.deleteByMessageId(messageId);
        messageRepository.deleteById(messageId);
    }


}
