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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Transactional
    public MessageDto create(MessageCreateRequest request, List<MultipartFile> attachments) {
        log.debug("메시지 생성 요청: channelId={}, authorId={}",
            request.channelId(), request.authorId());

        Channel channel = getChannelOrThrow(request.channelId());
        User author = getUserOrThrow(request.authorId());
        String content = request.content() != null ? request.content().strip() : null;
        Message message = messageRepository.save(new Message(content, channel, author));

        List<BinaryContent> binaryContents = saveAttachments(message, attachments);

        eventPublisher.publishEvent(new MessageCreatedEvent(message.getId()));

        return messageMapper.toDto(message, binaryContents);
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

        List<MessageAttachment> messageAttachments = IntStream.range(0, binaryContents.size())
            .mapToObj(i -> new MessageAttachment(message, binaryContents.get(i), i))
            .toList();
        messageAttachmentRepository.saveAll(messageAttachments);

        IntStream.range(0, binaryContents.size()).forEach(i ->
            eventPublisher.publishEvent(
                new BinaryContentCreatedEvent(binaryContents.get(i).getId(), allBytes.get(i)))
        );

        log.info("메시지 첨부파일 저장 완료: messageId={}, count={}", message.getId(), binaryContents.size());

        return binaryContents;
    }

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

        List<MessageAttachment> messageAttachments =
            messageAttachmentRepository.findAllWithAttachmentByMessageInOrderByOrderIndexAsc(messages);

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

        Message message = getMessageOrThrow(messageId);
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

        getMessageOrThrow(messageId);
        messageRepository.deleteById(messageId);

        log.info("메시지 삭제 완료: messageId={}", messageId);

        eventPublisher.publishEvent(new MessageDeletedEvent(messageId));
    }

    public boolean isAuthor(UUID messageId, UUID userId) {
        return messageRepository.findById(messageId)
            .map(Message::getAuthor)
            .map(author -> author.getId().equals(userId))
            .orElse(false);
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }

    private Message getMessageOrThrow(UUID messageId) {
        return messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException(messageId));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
