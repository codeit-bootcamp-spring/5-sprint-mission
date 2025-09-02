package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found: " + channelId);
        }
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("Author not found: " + authorId);
        }

        List<UUID> attachmentIds = binaryContentCreateRequests.stream()
                .map(request -> {
                    String fileName = request.fileName();
                    String contentType = request.contentType();
                    byte[] bytes = request.bytes();

                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                    return binaryContentRepository.save(binaryContent).getId();
                }).toList();

        String content = messageCreateRequest.content();
        Message message = new Message(channelId, authorId, content, attachmentIds);
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message not found: " + id));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Message update(UUID id, MessageUpdateRequest request) {
        Message message = find(id);
        String newContent = request.content();
        message.editContent(newContent);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = find(id);
        message.getAttachmentIds().forEach(binaryContentRepository::delete);
        messageRepository.delete(message.getId());
    }
}
