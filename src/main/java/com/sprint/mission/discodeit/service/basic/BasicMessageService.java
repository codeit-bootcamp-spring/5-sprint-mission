package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicMessageService(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message create(UUID channelId, UUID authorId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be null or blank.");
        }
        if (userRepository.findById(authorId).isEmpty()) {
            throw new IllegalArgumentException("User not found: " + authorId);
        }
        if (channelRepository.findById(channelId).isEmpty()) {
            throw new IllegalArgumentException("Channel not found: " + channelId);
        }

        Message message = new Message(channelId, authorId, content);
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found: " + messageId));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = find(messageId);
        message.update(content);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (messageRepository.findById(messageId).isEmpty()) {
            throw new NoSuchElementException("Message not found: " + messageId);
        }
        messageRepository.deleteById(messageId);
    }

    @Override
    public void clear() {
        messageRepository.clear();
    }
}
