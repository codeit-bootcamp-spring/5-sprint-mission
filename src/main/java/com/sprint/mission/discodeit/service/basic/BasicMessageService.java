package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
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
    public MessageResponseDto create(MessageCreateRequest request, List<BinaryContentCreateRequest> attachmentRequests) {

        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + request.channelId());
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("Author not found with id " + request.authorId());
        }

        List<UUID> attachmentIds = attachmentRequests.stream()
                .map(attachmentRequest -> {
                    BinaryContent binaryContent = new BinaryContent(
                            attachmentRequest.fileName(),
                            (long) (attachmentRequest.bytes() != null ? attachmentRequest.bytes().length : 0),
                            attachmentRequest.contentType(),
                            attachmentRequest.bytes()
                    );
                    BinaryContent savedContent = binaryContentRepository.save(binaryContent);
                    return savedContent.getId();
                })
                .toList();

        Message message = new Message(
                request.content(),
                request.channelId(),
                request.authorId(),
                attachmentIds
        );

        Message savedMessage = messageRepository.save(message);

        // DTO 로 반환
        return new MessageResponseDto(
                savedMessage.getId(),
                savedMessage.getContent(),
                savedMessage.getChannelId(),
                savedMessage.getAuthorId(),
                savedMessage.getAttachmentIds()
        );
    }


    @Override
    public MessageResponseDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        return new MessageResponseDto(
                message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(message -> new MessageResponseDto(
                        message.getId(),
                        message.getContent(),
                        message.getChannelId(),
                        message.getAuthorId(),
                        message.getAttachmentIds()
                ))
                .toList();
    }


    @Override
    public MessageResponseDto update(UUID MessageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + request.messageId() + " not found"));

        message.update(newContent);
        Message updatedMessage = messageRepository.save(message);

        return new MessageResponseDto(
                updatedMessage.getId(),
                updatedMessage.getContent(),
                updatedMessage.getChannelId(),
                updatedMessage.getAuthorId(),
                updatedMessage.getAttachmentIds()
        );
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        message.getAttachmentIds()
                .forEach(binaryContentRepository::deleteById);

        messageRepository.deleteById(messageId);
    }


}
