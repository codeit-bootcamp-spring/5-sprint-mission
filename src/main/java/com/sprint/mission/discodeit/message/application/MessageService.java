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
import org.springframework.data.domain.Slice;
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
import java.util.Optional;
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

        log.info("Attachments saved: [messageId={}, count={}]", message.getId(), binaryContents.size());

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

    // 성능 문제로 Total Element 제거 (Slice 사용)
    @Transactional(readOnly = true)
    public PaginationResponse<MessageDto> findAllByChannelId(
        UUID channelId,
        Instant cursor,
        PaginationRequest request
    ) {
        Slice<Message> slice = messageRepository.findSliceWithAuthorAndProfileByChannelIdAndCreatedAtBefore(
            channelId, Optional.ofNullable(cursor).orElse(Instant.now()), request.toPageRequest());
        if (slice.isEmpty()) {
            return PaginationResponse.empty();
        }

        List<Message> messages = slice.getContent();
        Map<UUID, List<BinaryContent>> attachmentMap = fetchAttachmentMap(messages);
        List<MessageDto> content = toMessageDtos(messages, attachmentMap);

        Instant nextCursor = slice.hasNext()
            ? content.get(content.size() - 1).createdAt()
            : null;

        return new PaginationResponse<>(content, nextCursor, slice.getSize(), slice.hasNext());
    }

    private Map<UUID, List<BinaryContent>> fetchAttachmentMap(List<Message> messages) {
        List<UUID> messageIds = messages.stream().map(Message::getId).toList();

        return messageAttachmentRepository
            .findAllWithAttachmentByMessageIdInOrderByOrderIndexAsc(messageIds).stream()
            .collect(Collectors.groupingBy(
                attachment -> attachment.getMessage().getId(),
                Collectors.mapping(MessageAttachment::getAttachment, Collectors.toList())
            ));
    }

    // UserDto 대신 캐싱 가능한 UserInfoDto 도입 후 조립 고려
    private List<MessageDto> toMessageDtos(
        List<Message> messages,
        Map<UUID, List<BinaryContent>> attachmentMap
    ) {
        Map<UUID, UserDto> userDtoCache = new HashMap<>();

        return messages.stream()
            .map(message -> {
                UserDto authorDto = resolveAuthorDto(message.getAuthor(), userDtoCache);
                List<BinaryContent> attachments = attachmentMap.getOrDefault(message.getId(), List.of());
                return messageMapper.toDtoWithAuthorDto(message, authorDto, attachments);
            })
            .toList();
    }

    private UserDto resolveAuthorDto(User author, Map<UUID, UserDto> cache) {
        if (author == null) {
            return null;
        }
        return cache.computeIfAbsent(author.getId(), id -> userMapper.toDto(author));
    }

    @PreAuthorize("@messageService.isAuthor(#messageId, authentication.principal.userDetailsDto.id)")
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        log.debug("Updating message: [messageId={}]", messageId);

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException(messageId));

        List<BinaryContent> attachments =
            messageAttachmentRepository.findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(messageId).stream()
                .map(MessageAttachment::getAttachment)
                .toList();

        if (request.newContent() != null) {
            if (request.newContent().isBlank() && attachments.isEmpty()) {
                throw new EmptyMessageContentException();
            }
            message.update(request.newContent().strip());
        }

        MessageDto result = messageMapper.toDto(message, attachments);

        log.info("Message updated: [messageId={}]", result.id());

        return result;
    }

    @PreAuthorize("@messageService.isAuthor(#messageId, authentication.principal.userDetailsDto.id)")
    @Transactional
    public void deleteById(UUID messageId) {
        log.debug("Deleting message: [messageId={}]", messageId);

        messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException(messageId));

        messageRepository.deleteById(messageId);

        eventPublisher.publishEvent(new MessageDeletedEvent(messageId));

        log.info("Message deleted: [messageId={}]", messageId);
    }

    public boolean isAuthor(UUID messageId, UUID userId) {
        return messageRepository.existsByIdAndAuthorId(messageId, userId);
    }
}
