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
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message create(String content, UUID userId, UUID channelId) {
        if (content == null || content.isBlank() || userId == null || channelId == null) {
            throw new IllegalArgumentException("Message info is invalid");
        }

        Message message = new Message(content, userId, channelId);
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId).orElseThrow(() -> new NoSuchElementException("Message not found"));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new NoSuchElementException("Message not found"));
        message.update(content);
        return messageRepository.save(message);
    }

    @Override
    public boolean delete(UUID messageId) {
        return messageRepository.delete(messageId);
    }
}
