package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    private final MessageMapper  messageMapper;

    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        User author = userRepository.findById(authorId).orElseThrow(() -> new NoSuchElementException("User with id " + authorId + " not found"));

        Set<BinaryContent> attachments = binaryContentCreateRequests.stream()
                .map(attachmentRequest -> {
                    String fileName = attachmentRequest.fileName();
                    String contentType = attachmentRequest.contentType();
                    byte[] bytes = attachmentRequest.bytes();

                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                })
                .collect(Collectors.toSet());

        String content = messageCreateRequest.content();
        Message message = new Message(content, channel, author, attachments);
        messageRepository.save(message);
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto find(UUID messageId) {
        return messageRepository.findById(messageId).map(messageMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

//    @Override
//    @Transactional(readOnly = true)
//    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable) {
//        Slice<Message> messageSlice = messageRepository.findAllByChannelIdOrderByCreatedAtDesc(channelId, pageable);
//
//        List<MessageDto> messageDtos = messageSlice.getContent().stream()
//                .map(messageMapper::toDto)
//                .toList();
//
//        return new PageResponse<>(
//                messageDtos,
//                messageSlice.getNumber(),
//                messageSlice.getSize(),
//                messageSlice.hasNext(),
//                null
//        );
//    }


    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Object cursor, int size) {
        Instant cursorTime = null;

        // cursor가 있으면 Instant로 변환
        if (cursor != null) {
            if (cursor instanceof String) {
                cursorTime = Instant.parse((String) cursor);
            } else if (cursor instanceof Instant) {
                cursorTime = (Instant) cursor;
            } else {
                throw new IllegalArgumentException("Invalid cursor format");
            }
        }

        // PageRequest 생성 (size + 1로 hasNext 판단)
        Pageable pageable = PageRequest.of(0, size + 1);

        List<Message> messages;
        if (cursorTime == null) {
            // 첫 번째 페이지 - cursor가 없는 경우
            messages = messageRepository.findAllByChannelIdOrderByCreatedAtDesc(channelId, pageable)
                    .getContent();
        } else {
            // cursor 이후의 데이터 조회
            messages = messageRepository.findAllByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(
                    channelId, cursorTime, pageable).getContent();
        }

        // hasNext 판단 및 실제 데이터 추출
        boolean hasNext = messages.size() > size;
        if (hasNext) {
            messages = messages.subList(0, size); // 마지막 요소 제거
        }

        // 다음 커서 설정 (마지막 메시지의 createdAt)
        Object nextCursor = null;
        if (hasNext && !messages.isEmpty()) {
            nextCursor = messages.get(messages.size() - 1).getCreatedAt().toString();
        }

        // DTO 변환
        List<MessageDto> messageDtos = messages.stream()
                .map(messageMapper::toDto)
                .toList();

        return new PageResponse<>(
                messageDtos,
                nextCursor,
                size,
                hasNext,
                null // 메시지의 경우 총 개수는 일반적으로 제공하지 않음 (성능상 이유)
        );
    }


    @Override
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(newContent);
        messageRepository.save(message);
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        binaryContentRepository.deleteAll(message.getAttachments());
        messageRepository.deleteById(messageId);
    }
}
