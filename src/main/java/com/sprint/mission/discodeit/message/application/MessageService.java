package com.sprint.mission.discodeit.message.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.domain.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentUploadException;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.common.presentation.dto.PaginationRequest;
import com.sprint.mission.discodeit.common.presentation.dto.PaginationResponse;
import com.sprint.mission.discodeit.message.domain.Message;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachment;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachmentRepository;
import com.sprint.mission.discodeit.message.domain.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import com.sprint.mission.discodeit.message.domain.exception.EmptyMessageContentException;
import com.sprint.mission.discodeit.message.domain.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.message.presentation.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.MessageDto;
import com.sprint.mission.discodeit.message.presentation.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.user.application.UserMapper;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.domain.exception.UserNotFoundException;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public MessageDto create(MessageCreateRequest request, List<MultipartFile> attachments) {
        UUID channelId = request.channelId();
        UUID authorId = request.authorId();

        log.debug("Creating message: [channelId={}, authorId={}]", channelId, authorId);

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new UserNotFoundException(authorId));

        String content = request.content() != null ? request.content().strip() : null;
        if ((content == null || content.isEmpty()) && (attachments == null || attachments.isEmpty())) {
            throw new EmptyMessageContentException();
        }

        Message message = messageRepository.save(new Message(content, channel, author));

        List<BinaryContent> binaryContents = saveAttachments(message, attachments);

        MessageDto result = messageMapper.toDto(message, binaryContents);

        eventPublisher.publishEvent(new MessageCreatedEvent(message.getId()));

        log.info("Message created: [messageId={}, channelId={}, authorId={}]",
            result.id(), result.channelId(), result.author().id());

        return result;
    }

    private List<BinaryContent> saveAttachments(Message message, List<MultipartFile> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return List.of();
        }

        List<byte[]> allBytes = readAllBytes(attachments);

        List<BinaryContent> binaryContents = attachments.stream()
            .map(file -> new BinaryContent(
                file.getOriginalFilename(), file.getSize(), file.getContentType()))
            .toList();
        binaryContentRepository.saveAll(binaryContents);

        List<MessageAttachment> messageAttachments = new ArrayList<>();
        for (int i = 0; i < binaryContents.size(); i++) {
            messageAttachments.add(new MessageAttachment(message, binaryContents.get(i), i));

            eventPublisher.publishEvent(
                new BinaryContentCreatedEvent(binaryContents.get(i).getId(), allBytes.get(i))
            );
        }
        messageAttachmentRepository.saveAll(messageAttachments);

        log.info("Attachments saved: messageId={}, count={}", message.getId(), binaryContents.size());

        return binaryContents;
    }

    // S3 direct upload 필요 (OOM)
    private List<byte[]> readAllBytes(List<MultipartFile> attachments) {
        return attachments.stream()
            .map(file -> {
                try {
                    return file.getBytes();
                } catch (IOException e) {
                    throw new BinaryContentUploadException(e);
                }
            })
            .toList();
    }

    @Transactional(readOnly = true)
    public PaginationResponse<MessageDto> findAllByChannelId(
        UUID channelId,
        Instant cursor,
        PaginationRequest pageable
    ) {
        PageRequest pageRequest = pageable.toPageRequest();

        Page<Message> page;
        if (cursor != null) {
            page = messageRepository.findPagedWithAuthorAndProfileByChannelIdAndCreatedAtBefore(channelId, cursor, pageRequest);
        } else {
            page = messageRepository.findPagedWithAuthorAndProfileByChannelId(channelId, pageRequest);
        }

        if (page.isEmpty()) {
            return new PaginationResponse<>(
                List.of(),
                null,
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
            );
        }

        List<Message> messages = page.getContent();
        List<UUID> messageIds = messages.stream().map(Message::getId).toList();

        List<MessageAttachment> messageAttachments =
            messageAttachmentRepository.findAllWithAttachmentByMessageIdInOrderByOrderIndexAsc(messageIds);

        Map<UUID, List<BinaryContent>> messageIdToAttachments = messageAttachments.stream()
            .collect(Collectors.groupingBy(
                messageAttachment -> messageAttachment.getMessage().getId(),
                Collectors.mapping(MessageAttachment::getAttachment, Collectors.toList())
            ));

        Map<UUID, UserDto> userDtoMap = new HashMap<>();
        List<MessageDto> result = messages.stream()
            .map(message -> {
                User author = message.getAuthor();
                UserDto authorDto = null;
                if (author != null) {
                    authorDto = userDtoMap.computeIfAbsent(
                        author.getId(),
                        id -> userMapper.toDto(author)
                    );
                }
                return messageMapper.toDtoWithAuthorDto(
                    message,
                    authorDto,
                    messageIdToAttachments.getOrDefault(message.getId(), List.of())
                );
            })
            .toList();

        Instant nextCursor = page.hasNext()
            ? result.get(result.size() - 1).createdAt()
            : null;

        return new PaginationResponse<>(
            result,
            nextCursor,
            page.getSize(),
            page.hasNext(),
            page.getTotalElements()
        );
    }

    @PreAuthorize("@messageService.isAuthor(#messageId, authentication.principal.userDto.id)")
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        log.debug("메시지 수정 요청: messageId={}", messageId);

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException(messageId));

        List<BinaryContent> attachments =
            messageAttachmentRepository.findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(messageId).stream()
                .map(MessageAttachment::getAttachment)
                .toList();

        if (request.newContent() != null) {
            message.update(request.newContent().strip());
        }

        log.info("메시지 수정 완료: messageId={}", messageId);

        return messageMapper.toDto(message, attachments);
    }

    @PreAuthorize("@messageService.isAuthor(#messageId, authentication.principal.userDto.id)")
    @Transactional
    public void deleteById(UUID messageId) {
        log.debug("메시지 삭제 요청: messageId={}", messageId);

        messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException(messageId));

        messageRepository.deleteById(messageId);

        eventPublisher.publishEvent(new MessageDeletedEvent(messageId));

        log.info("메시지 삭제 완료: messageId={}", messageId);
    }

    public boolean isAuthor(UUID messageId, UUID userId) {
        return messageRepository.findById(messageId)
            .map(Message::getAuthor)
            .map(author -> author.getId().equals(userId))
            .orElse(false);
    }
}
