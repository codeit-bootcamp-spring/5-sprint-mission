package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.main.Channel;
import com.sprint.mission.discodeit.entity.main.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.main.Message;
import com.sprint.mission.discodeit.entity.sub.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {
    private final MessageRepository messageRepository;

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;
    private final UserMapper userMapper;

    public MessageDto create(
            MessageCreateRequest messageCreateRequest,
            List<BinaryContentCreateRequest> binaryContentCreateRequests
    ) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("Author with id " + authorId + " does not exist"));

        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
                .map(req -> {
                    BinaryContent binaryContent = BinaryContent.builder()
                            .fileName(req.fileName())
                            .size((long) req.bytes().length)
                            .contentType(req.contentType())
                            .bytes(req.bytes())
                            .build();
                    return binaryContentRepository.save(binaryContent);
                })
                .toList();

        Message message = Message.builder()
                .content(messageCreateRequest.content())
                .channel(channel)
                .author(author)
                .attachments(attachments)
                .build();

        Message saved = messageRepository.save(message);
        return messageMapper.toDto(saved);
    }

    public MessageDto find(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(messageMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Slice<Message> slice = messageRepository.findAllByChannelId(channelId, pageable);

        return pageResponseMapper.fromSlice(slice.map(messageMapper::toDto));
    }

    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.setContent(newContent);
        return messageMapper.toDto(messageRepository.save(message));
    }

    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        binaryContentRepository.deleteAll(message.getAttachments());

        messageRepository.delete(message);
    }
}
