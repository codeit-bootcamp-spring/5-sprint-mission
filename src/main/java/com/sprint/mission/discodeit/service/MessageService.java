package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.response.Pageable;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final UserRepository userRepository;

    private final BinaryContentStorage binaryContentStorage;

    private final UserMapper userMapper;
    private final MessageMapper messageMapper;

    public PageResponse<MessageDto> findAllByChannelId(
        UUID channelId,
        Instant cursor,
        Pageable pageable
    ) {
        PageRequest pageRequest = Pageable.toPageRequest(pageable);

        Page<Message> page;
        if (cursor != null) {
            page = messageRepository.findPageByChannelId(channelId, cursor, pageRequest);
        } else {
            page = messageRepository.findPageWithoutCursorByChannelId(channelId, pageRequest);
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

        List<MessageAttachment> messageAttachmentList =
            messageAttachmentRepository.findAllByMessageIn(messages);

        Map<UUID, List<BinaryContent>> messageIdToAttachments = messageAttachmentList.stream()
            .collect(Collectors.groupingBy(
                ma -> ma.getMessage().getId(),
                Collectors.mapping(MessageAttachment::getAttachment, Collectors.toList())
            ));

        Map<UUID, UserDto> userCache = new HashMap<>();
        Instant onlineSince = Instant.now().minus(Duration.ofMinutes(5));

        List<MessageDto> result = messages.stream()
            .map(m -> {
                User author = m.getAuthor();
                UserDto authorDto = null;
                if (author != null) {
                    authorDto = userCache.computeIfAbsent(
                        author.getId(),
                        id -> userMapper.toDto(author, onlineSince)
                    );
                }
                return messageMapper.toDtoWithAuthorDto(
                    m,
                    authorDto,
                    messageIdToAttachments.get(m.getId())
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

    @Transactional
    public MessageDto create(MessageCreateRequest req, List<MultipartFile> attachments) {
        Channel channel = channelRepository.getOrThrow(req.channelId());
        User author = userRepository.getOrThrow(req.authorId());

        String content = req.content() != null ? req.content().strip() : null;

        Message m = messageRepository.save(new Message(content, channel, author));

        List<BinaryContent> binaryContents = new ArrayList<>();
        if (attachments != null && !attachments.isEmpty()) {

            int orderIndex = 0;
            for (MultipartFile attachment : attachments) {
                if (attachment == null || attachment.isEmpty()) {
                    continue;
                }

                BinaryContent bc = binaryContentRepository.save(
                    new BinaryContent(
                        attachment.getOriginalFilename(),
                        attachment.getSize(),
                        attachment.getContentType()
                    )
                );

                try {
                    binaryContentStorage.put(bc.getId(), attachment.getBytes());
                } catch (IOException e) {
                    throw new UncheckedIOException("첨부 파일 저장 실패: " + bc.getId(), e);
                }

                messageAttachmentRepository.save(new MessageAttachment(m, bc, orderIndex++));
                binaryContents.add(bc);
            }
        }

        return messageMapper.toDto(m, binaryContents);
    }

    @Transactional
    public void delete(UUID messageId) {
        messageRepository.getOrThrow(messageId);
        messageAttachmentRepository.deleteAllByMessageId(messageId);
        messageRepository.deleteById(messageId);
    }

    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest req) {
        Message m = messageRepository.getOrThrow(messageId);

        if (req.newContent() != null) {
            m.setContent(req.newContent().strip());
        }

        List<BinaryContent> attachments =
            messageAttachmentRepository.findAttachmentsByMessageId(messageId);

        return messageMapper.toDto(m, attachments);
    }
}
