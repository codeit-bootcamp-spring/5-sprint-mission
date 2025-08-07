package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
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

@RequiredArgsConstructor
@Service("BasicMessageService")
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest request) {
        if (!channelRepository.existsById(request.getChannelId())) {
            throw new NoSuchElementException("Channel not found with id " + request.getChannelId());
        }
        if (!userRepository.existsById(request.getAuthorId())) {
            throw new NoSuchElementException("Author not found with id " + request.getAuthorId());
        }

        Message message = new Message(
                request.getContent(),
                request.getChannelId(),
                request.getAuthorId()
        );

        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Message update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + request.getMessageId() + " not found"));

        message.update(request.getNewContent());

        return messageRepository.save(message);
    }

    @Override
    public boolean delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            return false;
        }

        binaryContentRepository.deleteAllByMessageId(messageId);
        messageRepository.deleteById(messageId);
        return true;
    }
}

