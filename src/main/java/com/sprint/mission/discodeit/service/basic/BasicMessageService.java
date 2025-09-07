package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.CursorPageResponse;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service("basicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Transactional
    @Override
    public MessageResponseDto create(MessageCreateRequest request, List<BinaryContentCreateRequest> attachmentRequests) {
        var channel = channelRepository.findById(request.channelId())
            .orElseThrow(() -> new NoSuchElementException("Channel not found with id " + request.channelId()));
        var author = userRepository.findById(request.authorId())
            .orElseThrow(() -> new NoSuchElementException("Author not found with id " + request.authorId()));

        List<BinaryContent> attachments = attachmentRequests.stream()
            .map(req -> new BinaryContent(
                req.fileName(),
                (long) (req.bytes() != null ? req.bytes().length : 0),
                req.contentType()
            ))
            .map(binaryContentRepository::save)
            .toList();

        Message message = new Message();
        message.setContent(request.content());
        message.setChannel(channel);
        message.setAuthor(author);
        message.setAttachments(attachments);

        Message savedMessage = messageRepository.save(message);
        return messageMapper.toDto(savedMessage);
    }

    @Transactional(readOnly = true)
    @Override
    public MessageResponseDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        return messageMapper.toDto(message);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
            .map(messageMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public MessageResponseDto update(UUID messageId, MessageUpdateRequest request) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(request.newContent());

        return messageMapper.toDto(message);
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        messageRepository.delete(message);
    }

    @Transactional(readOnly = true)
    @Override
    public CursorPageResponse<MessageResponseDto> findByCursor(UUID channelId, Instant cursor, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Message> messages = (cursor == null)
            ? messageRepository.findAllWithChannelAndAuthorByChannelId(channelId, pageable)
            : messageRepository.findTopNWithChannelAndAuthorByChannelId(channelId, cursor, pageable);

        List<MessageResponseDto> dtos = messages.stream()
            .map(messageMapper::toDto)
            .toList();

        boolean hasNext = dtos.size() == size;
        Instant lastCursor = hasNext ? dtos.get(dtos.size() - 1).createdAt() : null;

        return new CursorPageResponse<>(dtos, hasNext, lastCursor);
    }
}

