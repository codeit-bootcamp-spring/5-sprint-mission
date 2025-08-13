package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.message.AttachmentRequest;
import com.codeit.mission.discodeit.dto.message.MessageCreateRequest;
import com.codeit.mission.discodeit.dto.message.MessageResponse;
import com.codeit.mission.discodeit.dto.message.MessageUpdateRequest;
import com.codeit.mission.discodeit.entity.BinaryContent;
import com.codeit.mission.discodeit.entity.Message;
import com.codeit.mission.discodeit.repository.BinaryContentRepository;
import com.codeit.mission.discodeit.repository.ChannelRepository;
import com.codeit.mission.discodeit.repository.MessageRepository;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.service.MessageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("basicMessageService")
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    public BasicMessageService(@Qualifier("messageRepository") MessageRepository messageRepository,
                               @Qualifier("channelRepository") ChannelRepository channelRepository,
                               @Qualifier("userRepository") UserRepository userRepository,
                               @Qualifier("binaryContentRepository") BinaryContentRepository binaryContentRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        if (!channelRepository.existsById(request.getChannelId())) {
            throw new NoSuchElementException("Channel not found");
        }
        if (!userRepository.existsById(request.getAuthorId())) {
            throw new NoSuchElementException("User not found");
        }

        Message message = new Message(request.getContent(), request.getChannelId(), request.getAuthorId());
        Message savedMessage = messageRepository.save(message);

        List<UUID> attachmentIds = new ArrayList<>();
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            for (AttachmentRequest attachment : request.getAttachments()) {
                BinaryContent binaryContent = new BinaryContent(
                        attachment.getFileName(),
                        attachment.getContentType(),
                        attachment.getSize(),
                        attachment.getBytes(),
                        null, savedMessage.getId()
                );
                BinaryContent savedAttachment = binaryContentRepository.save(binaryContent);
                attachmentIds.add(savedAttachment.getId());
            }
        }

        return new MessageResponse(savedMessage, attachmentIds);
    }

    @Override
    public MessageResponse find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found"));

        List<UUID> attachmentIds = binaryContentRepository.findAll().stream()
                .filter(binaryContent -> messageId.equals(binaryContent.getAttachmentId()))
                .map(BinaryContent::getId)
                .collect(Collectors.toList());

        return new MessageResponse(message, attachmentIds);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findAll();

        return messages.stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(message -> {
                    List<UUID> attachmentIds = binaryContentRepository.findAll().stream()
                            .filter(binaryContent -> message.getId().equals(binaryContent.getAttachmentId()))
                            .map(BinaryContent::getId)
                            .collect(Collectors.toList());
                    return new MessageResponse(message, attachmentIds);
                })
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new NoSuchElementException("Message not found"));

        message.update(request.getContent());
        Message savedMessage = messageRepository.save(message);

        List<UUID> attachmentIds = binaryContentRepository.findAll().stream()
                .filter(binaryContent -> request.getMessageId().equals(binaryContent.getAttachmentId()))
                .map(BinaryContent::getId)
                .collect(Collectors.toList());
        return new MessageResponse(savedMessage, attachmentIds);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message not found");
        }

        List<BinaryContent> attachments = binaryContentRepository.findAll().stream()
                .filter(binaryContent -> messageId.equals(binaryContent.getAttachmentId()))
                .collect(Collectors.toList());

        for (BinaryContent attachment : attachments) {
            binaryContentRepository.deleteById(attachment.getId());
        }

        messageRepository.deleteById(messageId);
    }
}