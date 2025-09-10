package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageResponseMapper;

    @Transactional
    @Override
    public MessageDto create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("Author not found: " + authorId));

        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
                .map(request -> {
                    String fileName = request.fileName();
                    String contentType = request.contentType();
                    byte[] bytes = request.bytes();

                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);

                    // @GeneratedValue(strategy = GenerationType.UUID)는 애플리케이션 레벨에서 UUID 를 만든다는데 save 안 해도 id 있는지 추후 확인하기!!
                    // -> 메시지에서 바이너리컨텐트 cascade.All 하기 때문에 저장 굳이 X (위 내용이 맞으면 binaryContent.save 삭제!!)
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                }).toList();

        String content = messageCreateRequest.content();
        Message message = new Message(channel, author, content, attachments);
        return messageMapper.toDto(messageRepository.save(message));
    }

    @Override
    public MessageDto find(UUID id) {
        return messageRepository.findById(id)
                .map(messageMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Message not found: " + id));
    }

    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable) {
        Slice<MessageDto> slice = messageRepository.findAllByChannelId(channelId, pageable)
                .map(messageMapper::toDto);
        return pageResponseMapper.fromSlice(slice);
    }

    @Transactional
    @Override
    public MessageDto update(UUID id, MessageUpdateRequest request) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message not found: " + id));
        String newContent = request.newContent();
        message.editContent(newContent);
        return messageMapper.toDto(message);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!messageRepository.existsById(id)) {
            throw new NoSuchElementException("Message not found: " + id);
        }
        messageRepository.deleteById(id);
    }
}
