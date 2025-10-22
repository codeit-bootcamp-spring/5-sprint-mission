package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.data.MessageDto;
import com.codeit.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.dto.request.MessageCreateRequest;
import com.codeit.mission.discodeit.dto.request.MessageUpdateRequest;
import com.codeit.mission.discodeit.dto.response.PageResponse;
import com.codeit.mission.discodeit.entity.BinaryContent;
import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.entity.Message;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.mapper.MessageMapper;
import com.codeit.mission.discodeit.mapper.PageResponseMapper;
import com.codeit.mission.discodeit.repository.BinaryContentRepository;
import com.codeit.mission.discodeit.repository.ChannelRepository;
import com.codeit.mission.discodeit.repository.MessageRepository;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.service.MessageService;
import com.codeit.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentRepository binaryContentRepository;
    private final PageResponseMapper pageResponseMapper;

    @Transactional
    @Override
    public MessageDto create(MessageCreateRequest messageCreateRequest,
            List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        log.info("메시지 생성 요청 - channelId: {}, authorId: {}", channelId, authorId);

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Channel with id " + channelId + " does not exist"));
        User author = userRepository.findById(authorId)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Author with id " + authorId + " does not exist")
                );

        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
                .map(attachmentRequest -> {
                    String fileName = attachmentRequest.fileName();
                    String contentType = attachmentRequest.contentType();
                    byte[] bytes = attachmentRequest.bytes();

                    log.debug("첨부파일 업로드 처리 - fileName: {}, contentType: {}, size: {} bytes",
                            fileName, contentType, bytes.length);
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                            contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    log.debug("첨부파일 저장 완료 - binaryContentId: {}, fileName: {}",
                            binaryContent.getId(), fileName);
                    return binaryContent;
                })
                .toList();

        String content = messageCreateRequest.content();
        Message message = new Message(
                content,
                channel,
                author,
                attachments
        );

        messageRepository.save(message);
        log.info("메시지 생성 완료 - messageId: {}, channelId: {}, authorId: {}, 첨부파일 수: {}",
                message.getId(), channelId, authorId, attachments.size());
        return messageMapper.toDto(message);
    }

    @Transactional(readOnly = true)
    @Override
    public MessageDto find(UUID messageId) {
        log.debug("메시지 조회 요청 - messageId: {}", messageId);

        return messageRepository.findById(messageId)
                .map(messageMapper::toDto)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Message with id " + messageId + " not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt,
            Pageable pageable) {
        log.debug("채널별 메시지 목록 조회 요청 - channelId: {}, createAt: {}, pageSize: {}",
                channelId, createAt, pageable.getPageSize());

        Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(channelId,
                        Optional.ofNullable(createAt).orElse(Instant.now()),
                        pageable)
                .map(messageMapper::toDto);

        Instant nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1)
                    .createdAt();
        }

        log.info("채널별 메시지 목록 조회 완료 - channelId: {}, 조회된 메시지 수: {}, hasNext: {}",
                channelId, slice.getContent().size(), slice.hasNext());

        return pageResponseMapper.fromSlice(slice, nextCursor);
    }

    @Transactional
    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();

        log.info("메시지 수정 요청 - messageId: {}", messageId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Message with id " + messageId + " not found"));
        message.update(newContent);

        log.debug("메시지 수정 상세 정보 - messageId: {}, channelId: {}, authorId: {}",
                messageId, message.getChannel().getId(), message.getAuthor().getId());
        return messageMapper.toDto(message);
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        log.info("메시지 삭제 요청 - messageId: {}", messageId);

        if (!messageRepository.existsById(messageId)) {
            log.warn("메시지 삭제 실패 - 존재하지 않는 messageId: {}", messageId);
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }

        messageRepository.deleteById(messageId);
        log.info("메시지 삭제 완료 - messageId: {}", messageId);
    }
}
