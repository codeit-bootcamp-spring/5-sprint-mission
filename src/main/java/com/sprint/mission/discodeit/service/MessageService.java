package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("messageService")
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    public Message create(MessageCreateRequest request) {
        validate(request.authorId(), request.channelId());

        Message message = new Message(request.content(), request.channelId(), request.authorId());

        if(request.attachmentIds() != null && !request.attachmentIds().isEmpty()) {
            message.getAttachmentIds().addAll(request.attachmentIds());
        }

        return messageRepository.save(message);
    }

    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    public Message update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + request.messageId() + " not found"));
        message.update(request.content());

        return messageRepository.save(message);
    }

    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                        .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
        messageRepository.deleteById(message.getId());
    }

    public void deleteAll() {
        messageRepository.findAll().forEach(m -> delete(m.getId()));
    }

    private void validate(UUID authorId, UUID channelId) {
        if (!userRepository.existsById(authorId) || !channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 유저 또는 채널을 찾을 수 없습니다.");
        }
        if(authorId ==  null || channelId == null) {
            throw new IllegalArgumentException("authorId와 channelId는 null일 수 없습니다.");
        }
    }
}
