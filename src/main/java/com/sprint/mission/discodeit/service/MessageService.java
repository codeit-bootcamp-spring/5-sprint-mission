package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.data.MessageDto;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.pagination.request.Pageable;
import com.sprint.mission.discodeit.dto.pagination.response.PageResponse;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.message.MessageDeletedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentUploadException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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

    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByChannelId(
        UUID channelId,
        Instant cursor,
        Pageable pageable
    ) {
        PageRequest pageRequest = Pageable.toPageRequest(pageable);

        Page<Message> page;
        if (cursor != null) {
            page = messageRepository.findByChannelIdAndCreatedAtBefore(channelId, cursor, pageRequest);
        } else {
            page = messageRepository.findByChannelId(channelId, pageRequest);
        }

        if (page.isEmpty()) {
            return new PageResponse<>(
                List.of(),
                null,
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
            );
        }

        List<Message> messages = page.getContent();

        List<MessageAttachment> messageAttachments =
            messageAttachmentRepository.findByMessageInOrderByOrderIndexAsc(messages);

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

        return new PageResponse<>(
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

        if (request.newContent() != null) {
            message.update(request.newContent().strip());
        }

        List<BinaryContent> attachments =
            messageAttachmentRepository.findByMessageIdOrderByOrderIndexAsc(messageId).stream()
                .map(MessageAttachment::getAttachment).toList();

        log.info("메시지 수정 완료: messageId={}", messageId);

        return messageMapper.toDto(message, attachments);
    }

    @PreAuthorize("@messageService.isAuthor(#messageId, authentication.principal.userDto.id)")
    @Transactional
    public void delete(UUID messageId) {
        log.debug("메시지 삭제 요청: messageId={}", messageId);
        getMessageOrThrow(messageId);
        messageRepository.deleteById(messageId);
        eventPublisher.publishEvent(new MessageDeletedEvent(messageId));
        log.debug("메시지 삭제 완료: messageId={}", messageId);
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Message getMessageOrThrow(UUID messageId) {
        return messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException(messageId));
    }

    public boolean isAuthor(UUID messageId, UUID userId) {
        return messageRepository.findById(messageId)
            .map(msg -> msg.getAuthor().getId().equals(userId))
            .orElse(false);
    }

    private List<BinaryContent> saveAttachments(Message message, List<MultipartFile> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return List.of();
        }

        // 1. Fail-Fast: 모든 파일의 bytes를 먼저 읽어서 검증
        List<byte[]> allBytes = new ArrayList<>();
        for (MultipartFile attachment : attachments) {
            try {
                allBytes.add(attachment.getBytes());
            } catch (IOException e) {
                throw new BinaryContentUploadException(e);
            }
        }

        // 2. BinaryContent 메타데이터 일괄 저장
        List<BinaryContent> binaryContents = attachments.stream()
            .map(attachment -> new BinaryContent(
                attachment.getOriginalFilename(), attachment.getSize(), attachment.getContentType())
            )
            .toList();
        binaryContentRepository.saveAll(binaryContents);

        // 3. MessageAttachment 관계 일괄 저장
        List<MessageAttachment> messageAttachments = new ArrayList<>();
        for (int i = 0; i < binaryContents.size(); i++) {
            messageAttachments.add(new MessageAttachment(message, binaryContents.get(i), i));
        }
        messageAttachmentRepository.saveAll(messageAttachments);

        // 4. S3 업로드 이벤트 발행
        for (int i = 0; i < binaryContents.size(); i++) {
            BinaryContent binaryContent = binaryContents.get(i);

            log.debug("메시지 첨부파일 업로드 이벤트 발행: messageId={}, binaryContentId={}, filename={}",
                message.getId(), binaryContent.getId(), attachments.get(i).getOriginalFilename());

            eventPublisher.publishEvent(
                new BinaryContentCreatedEvent(binaryContent.getId(), allBytes.get(i))
            );
        }

        log.info("메시지 첨부파일 저장 완료: messageId={}, count={}", message.getId(), binaryContents.size());

        return binaryContents;
    }
}
